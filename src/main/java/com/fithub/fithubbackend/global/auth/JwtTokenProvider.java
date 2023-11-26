package com.fithub.fithubbackend.global.auth;

import com.fithub.fithubbackend.domain.user.application.UserDetailsServiceImpl;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import com.fithub.fithubbackend.global.util.RedisUtil;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Base64;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {


    @Value("${jwt.secret-key}")
    private String secretKey;

    private final UserDetailsServiceImpl userDetailsServiceImpl;

    private final RedisUtil redisUtil;
    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "Bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000L;              // 30분

    private static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;    // 7일

    @PostConstruct
    protected void init(){
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public boolean existsRefreshToken(String refreshToken) {
        String redisRefreshToken = redisUtil.getData(refreshToken);
        return StringUtils.hasText(redisRefreshToken);
    }

    public String createAccessToken(Authentication authentication) {
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
        return accessToken;
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
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .compact();

        return TokenInfoDto.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .refreshTokenExpirationTime(REFRESH_TOKEN_EXPIRE_TIME)
                .build();
    }


    public Authentication getAuthentication(String accessToken) {

        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken).getBody();
        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(claims.getSubject());

        return new UsernamePasswordAuthenticationToken(userDetails, accessToken, userDetails.getAuthorities());

    }

    public boolean validateToken(String token) throws JwtException {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            throw new JwtException(ErrorCode.WRONG_TYPE_TOKEN.getMessage());
        } catch (ExpiredJwtException e) {
            throw new JwtException(ErrorCode.EXPIRED_TOKEN.getMessage());
        } catch (UnsupportedJwtException e) {
            throw  new JwtException(ErrorCode.UNSUPPORTED_TOKEN.getMessage());
        } catch (IllegalArgumentException e) {
            throw new JwtException(ErrorCode.UNKNOWN_ERROR.getMessage());
        }
    }

    public Long getExpiration(String accessToken) {
        Date expiration = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken).getBody().getExpiration();
        Long now = new Date().getTime();
        return (expiration.getTime() - now);
    }


}