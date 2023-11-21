package com.fithub.fithubbackend.domain.user.dto;

import com.fithub.fithubbackend.domain.user.domain.Gender;
import com.fithub.fithubbackend.domain.user.domain.Grade;
import com.fithub.fithubbackend.domain.user.domain.Status;
import com.fithub.fithubbackend.domain.user.domain.User;
import lombok.Builder;

import java.time.LocalDateTime;

public class SignUpResponseDto {
    private String userId;
    private String name;
    private String nickname;
    private String email;
    private String phone;
    private Gender gender;
    private Grade grade;
    private Status status;
    private LocalDateTime createdDate;

    @Builder
    public SignUpResponseDto(User user){
        this.userId = user.getUserId();
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.gender = user.getGender();
        this.grade = user.getGrade();
        this.status = user.getStatus();
        this.createdDate = user.getCreatedDate();
    }
}
