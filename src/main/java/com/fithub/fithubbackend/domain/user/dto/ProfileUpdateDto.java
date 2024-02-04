package com.fithub.fithubbackend.domain.user.dto;

import com.fithub.fithubbackend.domain.user.enums.Gender;
import com.fithub.fithubbackend.domain.user.enums.Grade;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "프로필 수정 dto")
public class ProfileUpdateDto {
    private String name;
    private String nickname;
    private String phone;
    private Gender gender;
    private String bio;
}

