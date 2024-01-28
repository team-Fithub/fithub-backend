package com.fithub.fithubbackend.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class oAuthSignInDto {
    private String email;

    @Schema(description = "kakao or google or naver")
    private String provider;
}
