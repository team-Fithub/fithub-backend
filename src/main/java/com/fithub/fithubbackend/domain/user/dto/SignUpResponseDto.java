package com.fithub.fithubbackend.domain.user.dto;

import com.fithub.fithubbackend.domain.user.domain.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SignUpResponseDto {
    private String name;
    private String nickname;
    private String email;
    private String phone;
    private Document profileImgId;
    private String bio;
    private Gender gender;
    private Grade grade;
    private Status status;
    private LocalDateTime createdDate;

    @Builder
    public SignUpResponseDto(User user){
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.profileImgId = user.getProfileImgId();
        this.bio = user.getBio();
        this.gender = user.getGender();
        this.grade = user.getGrade();
        this.status = user.getStatus();
        this.createdDate = user.getCreatedDate();
    }
}
