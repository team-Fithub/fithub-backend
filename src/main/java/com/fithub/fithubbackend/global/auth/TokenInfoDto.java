package com.fithub.fithubbackend.global.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenInfoDto {

    private String grantType;
    private String accessToken;
    @JsonIgnore
    private String refreshToken;
    @JsonIgnore
    private Long refreshTokenExpirationTime;

    @Builder
    public TokenInfoDto(String grantType, String accessToken, String refreshToken, Long refreshTokenExpirationTime){
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.refreshTokenExpirationTime = refreshTokenExpirationTime;
    }


}