package com.fithub.fithubbackend.global.auth;

import com.fithub.fithubbackend.global.exception.ErrorCode;
import com.fithub.fithubbackend.global.util.RedisUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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

    public static final String ACCESS_TOKEN_COOKIE = "accessToken";
    public static final String REFRESH_TOKEN_COOKIE = "refreshToken";

    private final JwtTokenProvider jwtTokenProvider;

    private final RedisUtil redisUtil;


    private static final String[] SHOULD_NOT_FILTER_URI_ALL_LIST = new String[]{
            "/auth/sign-in", "/auth/sign-up", "/auth/oauth/regist", "exception",
            "/auth/reissue", "/admin/sign-in", "**exception**"
    };

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return Arrays.stream(SHOULD_NOT_FILTER_URI_ALL_LIST)
                .anyMatch(e -> new AntPathMatcher().match(e, request.getServletPath()));
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String accessToken = resolveAccessToken(request);
        String refreshToken = resolveRefreshToken(request);

        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
            String isLogout = redisUtil.getData(accessToken);
            if (ObjectUtils.isEmpty(isLogout)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } else if (accessToken == null && refreshToken != null) {
            if (jwtTokenProvider.validateToken(refreshToken)) {     // 리프레시 토큰이 유효할 경우 액세스 토큰 재발급
                createAccessToken(refreshToken, response);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private String resolveAccessToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (ACCESS_TOKEN_COOKIE.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String resolveRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (REFRESH_TOKEN_COOKIE.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public void createAccessToken(String refreshToken, HttpServletResponse response) throws IOException {
        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);

        response.setContentType("application/json;charset=UTF-8");
        JSONObject responseJson = new JSONObject();

        if (jwtTokenProvider.existsRefreshToken(authentication.getName())) {
            String newAccessToken = jwtTokenProvider.createAccessToken(authentication);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            response.setStatus(HttpStatus.CREATED.value());     // 201 성공적으로 access Token 재발급
            responseJson.put("message", "30분 유효 기간 지나 access Token 재발급 성공");
            responseJson.put("accessToken", newAccessToken);
            response.getWriter().print(responseJson);

            log.info("액세스 토큰 재발급 성공");
        }
        else {
            response.setStatus(ErrorCode.EXPIRED_REFRESH_TOKEN.getHttpStatus().value());
            responseJson.put("message", ErrorCode.EXPIRED_REFRESH_TOKEN.getMessage());
            responseJson.put("httpStatus", ErrorCode.EXPIRED_REFRESH_TOKEN.getHttpStatus());
            response.getWriter().print(responseJson);
        }
    };
}

