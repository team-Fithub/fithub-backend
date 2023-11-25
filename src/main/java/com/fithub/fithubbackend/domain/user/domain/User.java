package com.fithub.fithubbackend.domain.user.domain;

import com.fithub.fithubbackend.domain.user.dto.SignUpDto;
import com.fithub.fithubbackend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String password;

    @NotNull
    private String name;

    @NotNull
    private String nickname;

    @NotNull
    private String email;

    @NotNull
    private String phone;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Grade grade;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;

    private String bio;

    @OneToOne(optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "document_id")
    private Document profileImgId;

    @ElementCollection
    private List<String> roles = new ArrayList<>();

    @Builder
    public User(SignUpDto signUpDto, String encodedPassword, Document document) {
        this.password = encodedPassword;
        this.name = signUpDto.getName();
        this.nickname = signUpDto.getNickname();
        this.email = signUpDto.getEmail();
        this.phone = signUpDto.getPhone();
        this.gender = signUpDto.getGender();
        this.grade = Grade.NORMAL;
        this.status = Status.NORMAL;
        this.profileImgId = document;
        this.bio = signUpDto.getBio();
    }
}
