package com.fithub.fithubbackend.global.auth;

import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.domain.user.enums.Gender;

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
            .nickname((String) kakaoProfile.get("nickname"))
            .provider("kakao")
            .providerId("kakao_" + attributes.get("id"))
            .oAuthKakaoBuild();
    }),

    NAVER("naver", (attributes) -> {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return User.oAuthNaverBuilder()
                .nickname((String) response.get("nickname"))
                .email((String) response.get("email"))
                .phone(((String) response.get("mobile")).replace("-", ""))
                .name((String) response.get("name"))
                .gender(Gender.toGender((String) response.get("gender")))
                .provider("naver")
                .providerId("naver_" + response.get("id"))
                .oAuthNaverBuild();
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
