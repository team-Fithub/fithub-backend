package com.fithub.fithubbackend.global.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class HeaderUtil {

    public static final String ACCESS_TOKEN_COOKIE = "accessToken";
    public String resolveToken(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies)
                if (ACCESS_TOKEN_COOKIE.equals(cookie.getName()))
                    return cookie.getValue();
        }
        return null;
    }

}
