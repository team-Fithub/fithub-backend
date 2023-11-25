package com.fithub.fithubbackend.domain.user.application;

import com.fithub.fithubbackend.domain.user.dto.SignInDto;
import com.fithub.fithubbackend.domain.user.dto.SignOutDto;
import com.fithub.fithubbackend.domain.user.repository.UserRepository;
import com.fithub.fithubbackend.global.auth.JwtTokenProvider;
import com.fithub.fithubbackend.global.auth.TokenInfoDto;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import com.fithub.fithubbackend.global.util.CookieUtil;
import com.fithub.fithubbackend.global.util.HeaderUtil;
import com.fithub.fithubbackend.global.util.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RedisUtil redisUtil;
    private final CookieUtil cookieUtil;
    private final HeaderUtil headerUtil;

    @Transactional
    @Override
    public TokenInfoDto signIn(SignInDto signInDto, HttpServletResponse response) {

        if (userRepository.findByEmail(signInDto.getEmail()).isEmpty())
            throw new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 회원");

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(signInDto.getEmail(), signInDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        TokenInfoDto tokenInfoDto = jwtTokenProvider.createToken(authentication);

        // refreshToken, accessToken 쿠키에 저장
        cookieUtil.addRefreshTokenCookie(response, tokenInfoDto);
        cookieUtil.addAccessTokenCookie(response, tokenInfoDto.getAccessToken());

        // Redis에 Key(이메일):Value(refreshToken) 저장
        redisUtil.setData(authentication.getName(), tokenInfoDto.getRefreshToken(), tokenInfoDto.getRefreshTokenExpirationTime());

        return tokenInfoDto;
    }

    @Transactional
    @Override
    public void signOut(SignOutDto signOutDto, UserDetails userDetails,
                        HttpServletResponse response, HttpServletRequest request) {

        signOutDto.setEmail(userDetails.getUsername());
        String refreshToken = redisUtil.getData(signOutDto.getEmail());
        signOutDto.setRefreshToken(refreshToken);

        boolean validateToken = false;

        try {
            validateToken = jwtTokenProvider.validateToken(signOutDto.getAccessToken());
            if (!validateToken) {
                throw new CustomException(ErrorCode.INVALID_TOKEN);
            }
        } catch (Exception e) {
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(signOutDto.getAccessToken());

        if (redisUtil.getData(authentication.getName()) != null) {
            redisUtil.deleteData(signOutDto.getEmail());
        }

        if (validateToken) {
            Long expiration = jwtTokenProvider.getExpiration(signOutDto.getAccessToken());
            redisUtil.setData(signOutDto.getAccessToken(), "logout", expiration);
        }

        cookieUtil.deleteAccessTokenCookie(request, response);

    }

    @Transactional
    @Override
    public TokenInfoDto reissue(String cookieRefreshToken, HttpServletRequest request, HttpServletResponse response) {

        // 1. Request Header 에서 Access Token 추출
        String accessToken = headerUtil.resolveAccessToken(request);

        // 2. Refresh Token 검증
        if (!jwtTokenProvider.validateToken(cookieRefreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN, "Refresh Token 정보가 유효하지 않습니다.");
        }

        // 3. Access Token 에서 User email 추출
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

        // 4. Redis 에서 User email 을 기반으로 저장된 Refresh Token 추출
        String refreshToken = redisUtil.getData(authentication.getName());

        if(ObjectUtils.isEmpty(refreshToken)) {
            throw new CustomException(ErrorCode.UNKNOWN_ERROR);
        }

        if(!refreshToken.equals(cookieRefreshToken)) {
            throw  new CustomException(ErrorCode.TOKEN_NOT_EQUALS);
        }

        // 5. 새로운 토큰 생성
        TokenInfoDto tokenInfoDto = jwtTokenProvider.createToken(authentication);


        // 6. RefreshToken Redis 업데이트
        redisUtil.setData(authentication.getName(), tokenInfoDto.getRefreshToken(), tokenInfoDto.getRefreshTokenExpirationTime());

        // 7. Cookie 업데이트
        cookieUtil.updateCookie(request, response, tokenInfoDto);

        return tokenInfoDto;
    };

}

