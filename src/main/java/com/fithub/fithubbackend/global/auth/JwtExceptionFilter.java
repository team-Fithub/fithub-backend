package com.fithub.fithubbackend.global.auth;

import com.fithub.fithubbackend.global.exception.ErrorCode;
import com.fithub.fithubbackend.global.util.CookieUtil;
import com.fithub.fithubbackend.global.util.HeaderUtil;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final HeaderUtil headerUtil;
    private final CookieUtil cookieUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.setCharacterEncoding("utf-8");

        try {
            filterChain.doFilter(request, response);    // JwtAuthenticationFilter로 이동
        } catch (JwtException e) {
            String errorMsg = e.getMessage();

            if (errorMsg.equals(ErrorCode.UNKNOWN_ERROR.getMessage())) {
                setResponse(response, ErrorCode.UNKNOWN_ERROR);
            } else if (errorMsg.equals(ErrorCode.EXPIRED_TOKEN.getMessage())) {     // 토큰 만료 시
                try {
                    String refreshToken = headerUtil.resolveRefreshToken(request);

                    if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
                        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);

                        if (jwtTokenProvider.existsRefreshToken(authentication.getName())) {
                            String accessToken = jwtTokenProvider.createAccessToken(authentication);

                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            setResponseWithAccessToken(response, accessToken);

                            log.info("액세스 토큰 재발급 성공");
                        }
                        else
                            setResponse(response, ErrorCode.EXPIRED_REFRESH_TOKEN);

                    } else {
                        setResponse(response, ErrorCode.EXPIRED_REFRESH_TOKEN);
                    }
                } catch (JwtException jwtException) {     // Refresh Token 만료 시
                    setResponse(response, ErrorCode.EXPIRED_REFRESH_TOKEN);
                }
            } else if (errorMsg.equals(ErrorCode.WRONG_TYPE_TOKEN.getMessage())) {
                setResponse(response, ErrorCode.WRONG_TYPE_TOKEN);
            } else if (errorMsg.equals(ErrorCode.UNSUPPORTED_TOKEN.getMessage())) {
                setResponse(response, ErrorCode.UNSUPPORTED_TOKEN);
            }
        }
    }

    private void setResponse(HttpServletResponse response, ErrorCode code) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(code.getHttpStatus().value());

        JSONObject responseJson = new JSONObject();
        responseJson.put("code", code.getHttpStatus().value());
        responseJson.put("message", code.getMessage());
        responseJson.put("httpStatus", code.getHttpStatus());

        response.getWriter().print(responseJson);
    }

    private void setResponseWithAccessToken(HttpServletResponse response, String accessToken) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpStatus.CREATED.value());     // 201 성공적으로 access Token 재발급

        JSONObject responseJson = new JSONObject();
        responseJson.put("httpStatus", HttpStatus.CREATED);
        responseJson.put("code", HttpStatus.CREATED.value());
        responseJson.put("message", "30분 유효 기간 지나 access Token 재발급 성공");
        responseJson.put("accessToken", accessToken);
        response.getWriter().print(responseJson);
    }
}
