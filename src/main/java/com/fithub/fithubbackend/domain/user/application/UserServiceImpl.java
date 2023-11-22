package com.fithub.fithubbackend.domain.user.application;

import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.domain.user.dto.UserDto;
import com.fithub.fithubbackend.domain.user.repository.UserRepository;
import com.fithub.fithubbackend.global.auth.JwtTokenProvider;
import com.fithub.fithubbackend.global.auth.TokenInfoDto;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RedisTemplate redisTemplate;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저가 없습니다."));
    }

    private UserDetails createUserDetails(User user) {
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), user.getAuthorities());
    }

    @Transactional
    @Override
    public TokenInfoDto signIn(UserDto.SignInDto signInDto) {

        if (userRepository.findByEmail(signInDto.getEmail()).isEmpty())
            throw new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 회원");

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(signInDto.getEmail(), signInDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        TokenInfoDto tokenInfoDto = jwtTokenProvider.createToken(authentication);

        // Redis에 Key(이메일):Value(refreshToken) 저장
        redisTemplate.opsForValue()
                .set(authentication.getName(), tokenInfoDto.getRefreshToken(),
                        tokenInfoDto.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

        return tokenInfoDto;
    }

    @Transactional
    @Override
    public void signOut(UserDto.SignOutDto signOutDto) {

        String refreshToken = (String) redisTemplate.opsForValue().get(signOutDto.getEmail());
        signOutDto.setRefreshToken(refreshToken);

        boolean validateToken = false;

        try {
            validateToken = jwtTokenProvider.validateToken(signOutDto.getAccessToken());
            if (!validateToken) {
                redisTemplate.delete(signOutDto.getEmail());
                throw new CustomException(ErrorCode.FAIL_AUTHENTICATION);
            }
        } catch (Exception e) {
            redisTemplate.delete(signOutDto.getEmail());
            throw new CustomException(ErrorCode.TOKEN_EXPIRED);
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(signOutDto.getAccessToken());

        if (redisTemplate.opsForValue().get(authentication.getName()) != null) {
            redisTemplate.delete(authentication.getName());
        }

        if (validateToken) {
            Long expiration = jwtTokenProvider.getExpiration(signOutDto.getAccessToken());
            redisTemplate.opsForValue()
                    .set(signOutDto.getAccessToken(), "logout", expiration, TimeUnit.MILLISECONDS);
        }

    }


}

