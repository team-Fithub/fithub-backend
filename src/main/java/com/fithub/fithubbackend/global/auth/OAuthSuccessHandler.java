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

        String targetUrl = "";
        if (oAuth2User.getAuthorities().contains(new SimpleGrantedAuthority("GUEST"))) {
            targetUrl = UriComponentsBuilder.fromUriString(firstRedirect)
                    .build().toString();
        } else {
            TokenInfoDto tokenInfoDto = jwtTokenProvider.createToken(authentication);
            cookieUtil.addRefreshTokenCookie(response, tokenInfoDto);
            cookieUtil.addAccessTokenCookie(response, tokenInfoDto.getAccessToken());
            redisUtil.setData((String) oAuth2User.getAttribute("email"), tokenInfoDto.getRefreshToken(), tokenInfoDto.getRefreshTokenExpirationTime());

            targetUrl = UriComponentsBuilder.fromUriString(url)
                    .queryParam("accessToken", tokenInfoDto.getAccessToken())
                    .queryParam("refreshToken", tokenInfoDto.getRefreshToken())
                    .build().toString();
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

}
