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
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
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

    // TODO: redirect 주소 정해지면 수정 필요
    @Value("${spring.security.registration.redirect}")
    private String url;

    private final RedisUtil redisUtil;
    private final CookieUtil cookieUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        TokenInfoDto tokenInfoDto = jwtTokenProvider.createToken(authentication);
        cookieUtil.addRefreshTokenCookie(response, tokenInfoDto);
        cookieUtil.addAccessTokenCookie(response, tokenInfoDto.getAccessToken());

        redisUtil.setData((String) oAuth2User.getAttributes().get("email"), tokenInfoDto.getRefreshToken(), tokenInfoDto.getRefreshTokenExpirationTime());

        String targetUrl = UriComponentsBuilder.fromUriString(url)
                .queryParam("accessToken", tokenInfoDto.getAccessToken())
                .queryParam("refreshToken", tokenInfoDto.getRefreshToken())
                .build().toString();
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

}
