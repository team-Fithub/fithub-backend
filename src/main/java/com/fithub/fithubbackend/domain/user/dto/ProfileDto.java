package com.fithub.fithubbackend.domain.user.dto;

import com.fithub.fithubbackend.domain.user.enums.Gender;
import com.fithub.fithubbackend.domain.user.enums.Grade;
import com.fithub.fithubbackend.global.domain.Document;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProfileDto {
    private String nickname;
    private String email;
    private String phone;
    private Gender gender;
    private String bio;
    private String profileImg;
    private Grade grade;
}
