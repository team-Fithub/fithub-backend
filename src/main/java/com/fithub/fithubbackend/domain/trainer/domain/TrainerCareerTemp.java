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

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainerCareerTemp extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private TrainerCertificationRequest trainerCertificationRequest;

    @NotNull
    @Size(min = 2)
    private String company;
    @NotNull
    @Size(min = 2)
    private String work;

    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;

    @ColumnDefault("false")
    @NotNull
    private boolean working;

    @Builder
    public TrainerCareerTemp (TrainerCareerRequestDto dto) {
        this.company = dto.getCompany();
        this.work = dto.getWork();
        this.startDate = dto.getStartDate();
        this.endDate = dto.getEndDate();
        this.working = dto.isWorking();
    }

    public void updateRequest(TrainerCertificationRequest request) {
        this.trainerCertificationRequest = request;
        request.getCareerTempList().add(this);
    }
}
