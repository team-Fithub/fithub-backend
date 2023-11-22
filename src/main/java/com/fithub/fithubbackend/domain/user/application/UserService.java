package com.fithub.fithubbackend.domain.user.application;

import com.fithub.fithubbackend.domain.user.dto.UserDto;
import com.fithub.fithubbackend.global.auth.TokenInfoDto;
import org.springframework.security.core.Authentication;

public interface UserService {

    TokenInfoDto signIn(UserDto.SignInDto signInDto);

    void signOut(UserDto.SignOutDto signOutDto);
}
