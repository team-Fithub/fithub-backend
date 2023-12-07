package com.fithub.fithubbackend.global.auth;

import com.fithub.fithubbackend.global.util.RedisUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
            "/auth/sign-in", "/auth/sign-up", "exception",
            "/auth/reissue", "/admin/sign-in", "**exception**"
    };

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return Arrays.stream(SHOULD_NOT_FILTER_URI_ALL_LIST)
                .anyMatch(e -> new AntPathMatcher().match(e, request.getServletPath()));
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String accessToken = resolveAccessToken((HttpServletRequest) request);

        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
            String isLogout = redisUtil.getData(accessToken);
            if (ObjectUtils.isEmpty(isLogout)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
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
}

