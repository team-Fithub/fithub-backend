package com.fithub.fithubbackend.domain.admin.dto;

import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.domain.user.enums.Gender;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApplicantInfoDto {
    private Long userId;
    private String name;
    private String nickname;
    private String email;
    private String phone;
    private Gender gender;
    private String profileImg;

    @Builder
    public ApplicantInfoDto(User user) {
        this.userId = user.getId();
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.gender = user.getGender();
        this.profileImg = user.getProfileImg().getUrl();
    }
}
