package com.fithub.fithubbackend.global.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
public class OAuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Value("${spring.security.registration.fail.redirect}")
    private String url;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.error("소셜 로그인 실패: {} , endpoint: {}", exception.getMessage(), request.getServletPath());

        String targetUrl = UriComponentsBuilder.fromUriString(url)
                .queryParam("status", HttpStatus.BAD_REQUEST)
                .queryParam("message", exception.getMessage())
                .build()
                .encode().toString();
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
