package com.fithub.fithubbackend.domain.trainer.domain;

import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainerCertificationRequest extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "trainerCertificationRequest", cascade = CascadeType.ALL)
    private List<TrainerLicenseTempImg> licenseTempImgList;

    @NotNull
    @Size(min = 2)
    private String licenseNames;

    @OneToMany(mappedBy = "trainerCertificationRequest", cascade = CascadeType.ALL)
    private List<TrainerCareerTemp> careerTempList;

    @NotNull
    @ColumnDefault("false")
    private boolean rejected;

    @Builder
    public TrainerCertificationRequest(User user, String licenseNames) {
        this.user = user;
        this.licenseNames = licenseNames;
        this.licenseTempImgList = new ArrayList<>();
        this.careerTempList = new ArrayList<>();
    }

    public void requestReject() {
        this.rejected = true;
    }
}
