package com.fithub.fithubbackend.global.auth;

import com.fithub.fithubbackend.global.exception.ErrorCode;
import com.fithub.fithubbackend.global.util.HeaderUtil;
import com.fithub.fithubbackend.global.util.RedisUtil;
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
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    private final RedisUtil redisUtil;

    private final HeaderUtil headerUtil;


    private static final String[] SHOULD_NOT_FILTER_URI_ALL_LIST = new String[]{
            "/auth/sign-in", "/auth/sign-up", "/auth/oauth/**", "exception",
            "/admin/sign-in", "**exception**","/auth/email/**"
    };

    private static final String[] SHOULD_NOT_FILTER_GET_URI_LIST = new String[] {
            "/users/training", "/users/training/all", "/users/training/reviews", "/auth/oauth/login", "/posts/public/**"
    };

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        if (request.getMethod().equals("GET")) {
            return Arrays.stream(SHOULD_NOT_FILTER_GET_URI_LIST).anyMatch(e -> new AntPathMatcher().match(e, request.getServletPath()));
        }
        return Arrays.stream(SHOULD_NOT_FILTER_URI_ALL_LIST)
                .anyMatch(e -> new AntPathMatcher().match(e, request.getServletPath()));
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("[doFilterInternal] - 요청 path: {}", request.getServletPath());
        // 1. 헤더, 쿠키에서 token 추출
        String accessToken = headerUtil.resolveAccessToken(request);
        String refreshToken = headerUtil.resolveRefreshToken(request);

        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {   // 2. access Token 유효 시
            String isLogout = redisUtil.getData(accessToken);
            if (ObjectUtils.isEmpty(isLogout)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } else if (accessToken == null && refreshToken != null) {   // 3. access Token 존재 X / refresh 유효 시
            if (jwtTokenProvider.validateToken(refreshToken)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);

                if (jwtTokenProvider.existsRefreshToken(authentication.getName()))
                    createAccessToken(authentication, response);
                else
                    notExistRefreshToken(response);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    public void createAccessToken(Authentication authentication, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        JSONObject responseJson = new JSONObject();

        String newAccessToken = jwtTokenProvider.createAccessToken(authentication);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        response.setStatus(HttpStatus.CREATED.value());     // 201 성공적으로 access Token 재발급

        responseJson.put("httpStatus", HttpStatus.CREATED);
        responseJson.put("code", HttpStatus.CREATED.value());
        responseJson.put("message", "30분 유효 기간 지나 access Token 재발급 성공");
        responseJson.put("accessToken", newAccessToken);

        response.getWriter().print(responseJson);
        log.info("액세스 토큰 재발급 성공");
    }

    public void notExistRefreshToken(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        JSONObject responseJson = new JSONObject();

        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
        responseJson.put("httpStatus", ErrorCode.EXPIRED_TOKEN.getHttpStatus().value());
        responseJson.put("code", ErrorCode.EXPIRED_TOKEN.getHttpStatus());
        responseJson.put("message", "redis에 Token 존재 하지 않음");
        response.getWriter().print(responseJson);
    }
}