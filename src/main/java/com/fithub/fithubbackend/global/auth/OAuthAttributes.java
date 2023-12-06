package com.fithub.fithubbackend.global.auth;

import com.fithub.fithubbackend.domain.user.domain.User;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public enum OAuthAttributes {
    GOOGLE("google", (attributes) -> User.oAuthBuilder()
            .nickname((String) attributes.get("name"))
            .email((String) attributes.get("email"))
            .provider("google")
            .providerId("google_" + attributes.get("sub"))
            .oAuthBuild()),

    KAKAO("kakao", (attributes) -> {
        // kakao는 kakao_account에 유저정보가 있다. (email)
        Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");
        // kakao_account안에 또 profile이라는 JSON객체가 있다. (nickname, profile_image)
        Map<String, Object> kakaoProfile = (Map<String, Object>)kakaoAccount.get("profile");

        return User.oAuthKakaoBuilder()
            /* TODO developers권한이 없어 name 동의항목 설정 수정 시 변경예정
            .name((String)attributes.get("name")) */

            .name("name")
            .nickname((String) kakaoProfile.get("nickname"))

            /* TODO developers권한이 없어 email 동의항목 설정 수정 시 변경예정
            .email((String) kakaoAccount.get("email")) */

            .email("email")
            .provider("kakao")
            .providerId("kakao_" + attributes.get("id"))
            .oAuthKakaoBuild();
    });

    private final String registrationId;
    private final Function<Map<String, Object>, User> attributes;

    OAuthAttributes(String registrationId, Function<Map<String, Object>, User> attributes) {
        this.registrationId = registrationId;
        this.attributes = attributes;
    }

    public static User extract(String registrationId, Map<String, Object> attributes) {
        return Arrays.stream(values()).filter(provider -> registrationId.equals(provider.registrationId))
                .findFirst().orElseThrow(IllegalArgumentException::new).attributes.apply(attributes);
    }
}
