package com.fithub.fithubbackend.global.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class HeaderUtil {

    public static final String ACCESS_TOKEN_COOKIE = "accessToken";

    public static final String REFRESH_TOKEN_COOKIE = "refreshToken";


    public String resolveAccessToken(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies)
                if (ACCESS_TOKEN_COOKIE.equals(cookie.getName()))
                    return cookie.getValue();
        }
        return null;
    }

    public String resolveRefreshToken(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies)
                if (REFRESH_TOKEN_COOKIE.equals(cookie.getName()))
                    return cookie.getValue();
        }
        return null;
    }

}
