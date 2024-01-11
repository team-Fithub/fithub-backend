package com.fithub.fithubbackend.global.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class HeaderUtil {

    private static final String BEARER_TYPE = "Bearer";

    private static final String AUTHORIZATION_HEADER = "Authorization";

    public static final String REFRESH_TOKEN_COOKIE = "refreshToken";


    public String resolveAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
            return bearerToken.substring(7);
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
