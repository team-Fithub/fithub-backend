package com.fithub.fithubbackend.global.auth;

import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.domain.user.repository.UserRepository;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
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

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        boolean isGuest = oAuth2User.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_GUEST"));
        String accessToken = "";

        if (oAuth2User.getAttributes().get("provider").equals("naver")) {
            User user = userRepository.findByEmail((String) oAuth2User.getAttributes().get("email")).orElseThrow(() -> new UsernameNotFoundException("소셜 회원가입을 다시 진행해주십시오."));
            user.updateGuestToUser();
            isGuest = false;
        }

        if (!isGuest) {
            accessToken = setCookieAndRedis(response, authentication, oAuth2User.getAttribute("email")).getAccessToken();
        }

        String targetUrl = getTargetUrl(isGuest, oAuth2User, accessToken);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private TokenInfoDto setCookieAndRedis(HttpServletResponse response, Authentication authentication, String email) {
        TokenInfoDto tokenInfoDto = jwtTokenProvider.createToken(authentication);
        cookieUtil.addRefreshTokenCookie(response, tokenInfoDto);
        redisUtil.setData(email, tokenInfoDto.getRefreshToken(), tokenInfoDto.getRefreshTokenExpirationTime());
        return tokenInfoDto;
    }

    private String getTargetUrl(boolean isGuest, OAuth2User oAuth2User, String accessToken) {
        if (isGuest) {
            return getGuestTargetUrl(oAuth2User.getAttributes());
        }
        return getUserTargetUrl(accessToken);
    }


    private String getGuestTargetUrl(Map<String, Object> attributes) {
        return UriComponentsBuilder.fromUriString(firstRedirect)
                .queryParam("provider", attributes.get("provider"))
                .queryParam("email", attributes.get("email"))
                .queryParam("name", attributes.get("name"))
                .queryParam("phone", attributes.get("phone"))
                .queryParam("gender", attributes.get("gender"))
                .build()
                .encode().toString();
    }

    private String getUserTargetUrl(String accessToken) {
        return UriComponentsBuilder.fromUriString(url)
                .queryParam("accessToken", accessToken)
                .build().toString();
    }
}
