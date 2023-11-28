package com.fithub.fithubbackend.domain.user.application;

import com.fithub.fithubbackend.domain.user.dto.SignInDto;
import com.fithub.fithubbackend.domain.user.dto.SignOutDto;
import com.fithub.fithubbackend.domain.user.dto.SignUpDto;
import com.fithub.fithubbackend.domain.user.dto.SignUpResponseDto;
import com.fithub.fithubbackend.global.auth.TokenInfoDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;

public interface AuthService {
    ResponseEntity<SignUpResponseDto> signUp(@Valid SignUpDto signUpDto, BindingResult bindingResult);
    
    TokenInfoDto signIn(SignInDto signInDto, HttpServletResponse response);

    void signOut(SignOutDto signOutDto, UserDetails userDetails, HttpServletResponse response, HttpServletRequest request);

    TokenInfoDto reissue(String cookieRefreshToken, HttpServletRequest request, HttpServletResponse response);
}
