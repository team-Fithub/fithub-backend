package com.fithub.fithubbackend.global.auth;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenInfoDto {

    private String grantType;
    private String accessToken;
    private String refreshToken;
    private Long refreshTokenExpirationTime;

    @Builder
    public TokenInfoDto(String grantType, String accessToken, String refreshToken, Long refreshTokenExpirationTime){
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.refreshTokenExpirationTime = refreshTokenExpirationTime;
    }


}