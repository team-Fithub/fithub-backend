package com.fithub.fithubbackend.domain.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class UserDto {

    @Data
    public static class SignInDto {

        @NotNull
        private String email;

        @NotNull
        private String password;
    }

    @Data
    public static class SignOutDto {

        @NotNull
        private String accessToken;

        private String email;

        private String refreshToken;
    }



}
