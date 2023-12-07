package com.fithub.fithubbackend.global.auth;

import com.fithub.fithubbackend.global.util.CookieUtil;
import com.fithub.fithubbackend.global.util.RedisUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${spring.security.registration.redirect}")
    private String url;

    @Value("${spring.security.registration.first.redirect}")
    private String firstRedirect;

    private final RedisUtil redisUtil;
    private final CookieUtil cookieUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        boolean isGuest = oAuth2User.getAuthorities().contains(new SimpleGrantedAuthority("GUEST"));

        String targetUrl = getTargetUrl(isGuest, oAuth2User);
        if (!isGuest) {
            setCookieAndRedis(response, authentication, oAuth2User.getAttribute("email"));
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private void setCookieAndRedis(HttpServletResponse response, Authentication authentication, String email) {
        TokenInfoDto tokenInfoDto = jwtTokenProvider.createToken(authentication);
        cookieUtil.addRefreshTokenCookie(response, tokenInfoDto);
        cookieUtil.addAccessTokenCookie(response, tokenInfoDto.getAccessToken());
        redisUtil.setData(email, tokenInfoDto.getRefreshToken(), tokenInfoDto.getRefreshTokenExpirationTime());
    }

    private String getTargetUrl(boolean isGuest, OAuth2User oAuth2User) {
        if (isGuest) {
            return getGuestTargetUrl(oAuth2User.getAttributes());
        }
        return getUserTargetUrl();
    }

    private String getGuestTargetUrl(Map<String, Object> attributes) {
        return UriComponentsBuilder.fromUriString(firstRedirect)
                .queryParam("provider", attributes.get("provider"))
                .queryParam("id", attributes.get("id"))
                .queryParam("email", attributes.get("email"))
                .queryParam("nickname", attributes.get("nickname"))
                .queryParam("profileImg", attributes.get("profileImg"))
                .queryParam("phone", attributes.get("phone"))
                .queryParam("gender", attributes.get("gender"))
                .build().toString();
    }

    private String getUserTargetUrl() {
        return UriComponentsBuilder.fromUriString(url)
                .build().toString();
    }
}
