package com.fithub.fithubbackend.domain.trainer.domain;


import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.global.common.BaseTimeEntity;
import com.fithub.fithubbackend.global.domain.Document;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.locationtech.jts.geom.Point;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Trainer extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @NotNull
    @Size(min = 2)
    private String name;

    @NotNull
    private String email;

    @NotNull
    private String profileUrl;

    @Comment("현재 일하는 장소")
    private String address;

    private Point point;

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TrainerCareer> trainerCareerList;

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TrainerLicenseImg> trainerLicenseImgList;

    @Comment("전문 분야")
    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL)
    private List<TrainerExpertise> trainerExpertiseList;

    @Builder
    public Trainer(User user) {
        this.user = user;
        this.name = user.getName();
        this.email = user.getEmail();
        this.profileUrl = user.getProfileImg().getUrl();
    }

    public void updateAddress(TrainerCareer career) {
        this.address = career.getAddress();
        this.point = career.getPoint();
    }
    
    public void updateCareerList(List<TrainerCareer> trainerCareerList) {
        this.trainerCareerList = trainerCareerList;
    }

    public void updateTrainerLicenseImg(List<TrainerLicenseImg> trainerLicenseImgList) {
        this.trainerLicenseImgList = trainerLicenseImgList;
    }

    public void updateTrainerExpertise(List<TrainerExpertise> trainerExpertiseList) {
        this.trainerExpertiseList = trainerExpertiseList;
    }

    public void linkedToUserName(String name) {
        this.name = name;
    }

    public void linkedToUserProfileImg(Document document) {
        this.profileUrl = document.getUrl();
    }

    public void grantPermission() {
        this.user.getRoles().add("ROLE_TRAINER");
    }
}
