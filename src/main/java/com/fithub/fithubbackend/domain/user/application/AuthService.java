package com.fithub.fithubbackend.domain.user.application;

import com.fithub.fithubbackend.domain.user.dto.*;
import com.fithub.fithubbackend.global.auth.TokenInfoDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AuthService {
    ResponseEntity<SignUpResponseDto> signUp(SignUpDto signUpDto, MultipartFile profileImg, BindingResult bindingResult) throws IOException;
    
    TokenInfoDto signIn(SignInDto signInDto, HttpServletResponse response);

    void signOut(SignOutDto signOutDto, HttpServletResponse response, HttpServletRequest request);

    TokenInfoDto reissue(String cookieRefreshToken, HttpServletRequest request, HttpServletResponse response);

    String oAuthSignUp(OAuthSignUpDto oAuthSignUpDto, HttpServletResponse response);
}
