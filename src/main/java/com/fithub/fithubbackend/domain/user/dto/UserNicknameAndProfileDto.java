package com.fithub.fithubbackend.domain.user.dto;

import com.fithub.fithubbackend.domain.user.domain.User;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserNicknameAndProfileDto {
    private Long userId;
    private String nickname;
    private String profileUrl;

    public static UserNicknameAndProfileDto toDto(User user) {
        return UserNicknameAndProfileDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileUrl(user.getProfileImg().getUrl())
                .build();
    }
}
