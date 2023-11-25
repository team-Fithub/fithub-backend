package com.fithub.fithubbackend.domain.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SignOutDto {

    @NotNull
    private String accessToken;

    private String email;

    private String refreshToken;

    @Builder
    public SignOutDto (String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}