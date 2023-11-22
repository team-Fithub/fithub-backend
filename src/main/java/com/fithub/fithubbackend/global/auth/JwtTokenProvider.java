package com.fithub.fithubbackend.global.auth;

import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider {


    @Value("${jwt.secret-key}")
    private String secretKey;

    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "Bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000L;              // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;    // 7일


    @PostConstruct
    protected void init(){
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public TokenInfoDto createToken(Authentication authentication) {

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();

        Date accessTokenExpiresIn = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();


        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        return TokenInfoDto.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .refreshTokenExpirationTime(REFRESH_TOKEN_EXPIRE_TIME)
                .build();
    }

    public Authentication getAuthentication(String accessToken) {

        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());


        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean validateToken(String token) throws JwtException {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {     // 유효하지 않는 구성의 JWT 토큰
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {   // 만료된 JWT 토큰
            log.info("Expired JWT Token", e);
            throw new JwtException("만료된 토큰");
        } catch (UnsupportedJwtException e) {   // 지원되지 않는 형식이거나 구성의 JWT 토큰
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {   // accessToken 만료된 경우
            return e.getClaims();
        } catch (JwtException e) {      // accessToken 변조된 경우
             return null;      // TODO 변조된 경우 반환 타입 변환
        }
    }

    public Long getExpiration(String accessToken) {
        Date expiration = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken).getBody().getExpiration();
        Long now = new Date().getTime();
        System.out.printf(String.valueOf(expiration.getTime() - now));
        return (expiration.getTime() - now);
    }
}