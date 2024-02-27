package com.fithub.fithubbackend.domain.trainer.domain;

import com.fithub.fithubbackend.domain.trainer.dto.TrainerCareerRequestDto;
import com.fithub.fithubbackend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.locationtech.jts.geom.Point;


import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainerCareer extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Trainer trainer;

    @NotNull
    @Size(min = 2)
    private String company;

    @NotNull
    private String address;

    @Column(columnDefinition = "point")
    private Point point;

    @NotNull
    @Size(min = 2)
    private String work;

    @NotNull
    private LocalDate startDate;

    private LocalDate endDate;

    @ColumnDefault("false")
    @NotNull
    private boolean working;

    @Builder
    public TrainerCareer(Trainer trainer, TrainerCareerTemp trainerCareerTemp) {
        this.trainer = trainer;
        this.company = trainerCareerTemp.getCompany();
        this.address = trainerCareerTemp.getAddress();
        this.point = trainerCareerTemp.getPoint();
        this.work = trainerCareerTemp.getWork();
        this.startDate = trainerCareerTemp.getStartDate();
        this.endDate = trainerCareerTemp.getEndDate() != null ? trainerCareerTemp.getEndDate() : null;
        this.working = trainerCareerTemp.isWorking();
    }

    @Builder(builderMethodName = "careerBuilder", buildMethodName = "careerBuild")
    public TrainerCareer(Trainer trainer, TrainerCareerRequestDto dto, Point point) {
        this.trainer = trainer;
        this.company = dto.getCompany();
        this.address = dto.getAddress();
        this.point = point;
        this.work = dto.getWork();
        this.working = dto.isWorking();
        this.startDate = dto.getStartDate();
        this.endDate = dto.getEndDate() != null ? dto.getEndDate() : null;
    }

    public void updateCareer(TrainerCareerRequestDto dto, Point point) {
        this.company = dto.getCompany();
        this.address = dto.getAddress();
        this.point = point;
        this.work = dto.getWork();
        this.startDate = dto.getStartDate();
        this.endDate = dto.getEndDate() != null ? dto.getEndDate() : null;
    }

    public void joinCompany() {
        this.working = true;
    }

    public void quitCompany() {
        this.working = false;
    }
}
