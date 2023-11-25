package com.fithub.fithubbackend.domain.user.application;

import com.fithub.fithubbackend.domain.user.dto.SignInDto;
import com.fithub.fithubbackend.domain.user.dto.SignOutDto;
import com.fithub.fithubbackend.global.auth.TokenInfoDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {

    TokenInfoDto signIn(SignInDto signInDto, HttpServletResponse response);

    void signOut(SignOutDto signOutDto, UserDetails userDetails, HttpServletResponse response, HttpServletRequest request);

    TokenInfoDto reissue(String cookieRefreshToken, HttpServletRequest request, HttpServletResponse response);
}
