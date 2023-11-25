package com.fithub.fithubbackend.global.util;

import com.fithub.fithubbackend.global.auth.TokenInfoDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
@NoArgsConstructor
public class CookieUtil {

    private Cookie[] cookies;
    private Cookie cookie;

    public CookieUtil(HttpServletRequest request) {
        this.cookies = request.getCookies();
    }

    public String getValue(String name) {

        for(int i = 0; i < this.cookies.length; i++) {
            if(cookies[i].getName().equals(name)) {
                return cookies[i].getValue();
            }
        }
        return null;
    }

    @Description("refresh Token 쿠키 생성")
    public HttpServletResponse addRefreshTokenCookie(HttpServletResponse response, TokenInfoDto tokenInfoDto) {

        ResponseCookie cookie = ResponseCookie.from("refreshToken", tokenInfoDto.getRefreshToken())
                .maxAge(tokenInfoDto.getRefreshTokenExpirationTime())
                .path("/")
                .sameSite("None")
                .httpOnly(true)
//                .secure(true)  //    HTTPS 프로토콜에서만 쿠키 전송 가능
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
        return response;
    }

    @Description("access Token 쿠키 생성")
    public HttpServletResponse addAccessTokenCookie(HttpServletResponse response, TokenInfoDto tokenInfoDto) {


        ResponseCookie cookie = ResponseCookie.from("accessToken", tokenInfoDto.getAccessToken())
                .maxAge(1800)
                .path("/")
                .sameSite("None")
                .httpOnly(true)
//                .secure(true)  //    HTTPS 프로토콜에서만 쿠키 전송 가능
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
        return response;
    }

    @Description("refresh token 쿠키 삭제")
    public void deleteRefreshTokenCookie(HttpServletRequest request, HttpServletResponse response) {
        Optional<Cookie> refreshTokenCookie = Arrays
                .stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("refreshToken")).findFirst();

        if (refreshTokenCookie.isPresent()) {
            refreshTokenCookie.get().setMaxAge(0);
            refreshTokenCookie.get().setValue("");
            response.addCookie(refreshTokenCookie.get());
        }

    }

    @Description("access token 쿠키 삭제")
    public void deleteAccessTokenCookie(HttpServletRequest request, HttpServletResponse response) {
        Optional<Cookie> refreshTokenCookie = Arrays
                .stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("accessToken")).findFirst();

        if (refreshTokenCookie.isPresent()) {
            refreshTokenCookie.get().setMaxAge(0);
            refreshTokenCookie.get().setValue("");
            response.addCookie(refreshTokenCookie.get());
        }

    }

    @Description("쿠키 업데이트")
    public void updateCookie(HttpServletRequest request, HttpServletResponse response, TokenInfoDto tokenInfoDto) {
        Optional<Cookie> refreshTokenCookie = Arrays
                .stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("refreshToken")).findFirst();

        Optional<Cookie> accessTokenCookie = Arrays
                .stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("accessToken")).findFirst();

        if (refreshTokenCookie.isPresent()) {
            refreshTokenCookie.get().setMaxAge(Math.toIntExact(tokenInfoDto.getRefreshTokenExpirationTime()));
            refreshTokenCookie.get().setValue(tokenInfoDto.getRefreshToken());
            refreshTokenCookie.get().setPath("/");
            refreshTokenCookie.get().setHttpOnly(true);
            response.addCookie(refreshTokenCookie.get());
        }

        if (accessTokenCookie.isPresent()) {
            accessTokenCookie.get().setMaxAge(1800);
            accessTokenCookie.get().setValue(tokenInfoDto.getAccessToken());
            accessTokenCookie.get().setPath("/");
            accessTokenCookie.get().setHttpOnly(true);
            response.addCookie(accessTokenCookie.get());
        }

    }

    public CookieUtil addCookie(String key, String value) {
        this.cookie = new Cookie(key, value);
        return this;
    }

    public CookieUtil setExpire(int period) {
        this.cookie.setMaxAge(period);
        return this;
    }

    public CookieUtil setHttpOnly(boolean setHttpOnly) {
        this.cookie.setHttpOnly(setHttpOnly);
        return this;
    }

    public Cookie build() {
        return this.cookie;
    }
}