package com.fithub.fithubbackend.domain.user.dto;

import com.fithub.fithubbackend.domain.user.domain.*;
import com.fithub.fithubbackend.domain.user.enums.Gender;
import com.fithub.fithubbackend.domain.user.enums.Grade;
import com.fithub.fithubbackend.domain.user.enums.Status;
import com.fithub.fithubbackend.global.common.Category;
import com.fithub.fithubbackend.global.domain.Document;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

import java.util.List;

@Getter
public class SignUpResponseDto {
    private String name;
    private String nickname;
    private String email;
    private String phone;
    private Document profileImg;
    private String bio;
    private Gender gender;
    private Grade grade;
    private Status status;
    private LocalDateTime createdDate;
    private List<Category> interests;

    @Builder
    public SignUpResponseDto(User user){
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.profileImg = user.getProfileImg();
        this.bio = user.getBio();
        this.gender = user.getGender();
        this.grade = user.getGrade();
        this.status = user.getStatus();
        this.createdDate = user.getCreatedDate();
        this.interests = user.getInterests().stream().map(UserInterest::getInterest).toList();
    }
}
