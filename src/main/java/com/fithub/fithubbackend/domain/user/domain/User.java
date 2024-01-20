package com.fithub.fithubbackend.domain.user.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fithub.fithubbackend.domain.user.dto.OAuthSignUpDto;
import com.fithub.fithubbackend.domain.user.dto.ProfileDto;
import com.fithub.fithubbackend.domain.user.dto.SignUpDto;
import com.fithub.fithubbackend.domain.user.enums.Gender;
import com.fithub.fithubbackend.domain.user.enums.Grade;
import com.fithub.fithubbackend.domain.user.enums.Status;
import com.fithub.fithubbackend.global.common.BaseTimeEntity;
import com.fithub.fithubbackend.global.domain.Document;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
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

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "document_id")
    private Document profileImg;

    @ElementCollection
    private List<String> roles = new ArrayList<>();

    // oauth2
    private String provider;
    private String providerId;

    @Builder
    public User(SignUpDto signUpDto, String encodedPassword, Document document) {
        this.password = encodedPassword;
        this.name = signUpDto.getName();
        this.nickname = signUpDto.getNickname();
        this.email = signUpDto.getEmail();
        this.phone = signUpDto.getPhone();
        this.gender = signUpDto.getGender();
        this.grade = Grade.INTRODUCTORY;
        this.status = Status.NORMAL;
        this.profileImg = document;
        this.bio = signUpDto.getBio();
        this.roles = Collections.singletonList("ROLE_USER");
    }
    @Builder(builderMethodName = "oAuthBuilder", buildMethodName = "oAuthBuild")
    public User (String nickname, String email, String provider, String providerId) {
        this.name = "";
        this.nickname = nickname;
        this.email = email;
        this.provider = provider;
        this.providerId = providerId;
        this.roles = Collections.singletonList("ROLE_GUEST");
        this.grade = Grade.INTRODUCTORY;
        this.status = Status.NORMAL;
        this.gender = Gender.UNDEFINED;
        this.phone = "";
    }
    @Builder(builderMethodName = "oAuthKakaoBuilder", buildMethodName = "oAuthKakaoBuild")
    public User (String nickname, String provider, String providerId, Document profileImg) {
        this.name = "";
        this.nickname = nickname;
        this.email = "";
        this.provider = provider;
        this.providerId = providerId;
        this.roles = Collections.singletonList("ROLE_GUEST");
        this.profileImg = profileImg;
        this.grade = Grade.INTRODUCTORY;
        this.status = Status.NORMAL;
        this.gender = Gender.UNDEFINED;
        this.phone = "";
    }

    @Builder(builderMethodName = "oAuthNaverBuilder", buildMethodName = "oAuthNaverBuild")
    public User (String nickname, String email, String provider, String providerId, String name, Gender gender, String phone) {
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.provider = provider;
        this.providerId = providerId;
        this.roles = Collections.singletonList("ROLE_GUEST");
        this.grade = Grade.INTRODUCTORY;
        this.status = Status.NORMAL;
        this.gender = gender;
        this.phone = phone;
    }

    public void setOAuthSignUp(OAuthSignUpDto oAuthSignUpDto) {
        this.email = oAuthSignUpDto.getEmail();
        this.name = oAuthSignUpDto.getName();
        this.phone = oAuthSignUpDto.getPhone();
        this.bio = oAuthSignUpDto.getBio();
        this.gender = oAuthSignUpDto.getGender();
    }

    public void updateProfile(ProfileDto dto) {
        if(dto.getNickname() != null) this.nickname = dto.getNickname();
        if(dto.getEmail() != null) this.email = dto.getEmail();
        if(dto.getPhone() != null) this.phone = dto.getPhone();
        if(dto.getBio() != null) this.nickname = dto.getBio();
    }
    public void updateProfileImg(Document profileImg) {
        this.profileImg = profileImg;
    }

    public void updateGuestToUser() {
        this.roles.set(this.roles.indexOf("ROLE_GUEST"), "ROLE_USER");
    }

    public void updatePassword(String password) {
        this.password =password;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public String getUsername() {
        return this.email;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isEnabled() {
        return true;
    }
}
