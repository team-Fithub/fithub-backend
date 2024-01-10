package com.fithub.fithubbackend.global.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import com.fithub.fithubbackend.global.exception.ErrorResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class OAuthFailureHandler implements AuthenticationFailureHandler  {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.error("소셜 로그인 실패: {} , endpoint: {}", exception.getMessage(), request.getServletPath());
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponseDto errorResponseDto = ErrorResponseDto.toResponseEntity(ErrorCode.BAD_REQUEST, exception.getMessage()).getBody();

        response.setStatus(400);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponseDto));
    }
}
