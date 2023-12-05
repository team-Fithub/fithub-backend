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

    NAVER("naver", (attributes) -> {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return User.oAuthNaverBuilder()
                .nickname((String) response.get("nickname"))
                .email((String) response.get("email"))
                .phone(response.get("mobile") != null ? ((String) response.get("mobile")).replaceAll("-", "") : "")
                .name(response.get("name") != null ? (String) response.get("name") : (String) response.get("nickname"))
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
