package com.fithub.fithubbackend.domain.trainer.dto;

import com.fithub.fithubbackend.domain.trainer.domain.TrainerCareer;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainerCareerDto {

    @NotNull
    private Long careerId;

    @NotNull
    private String company;

    @NotNull
    private String address;

    @NotNull
    private String work;

    @NotNull
    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull
    private boolean working;

    @Builder
    public TrainerCareerDto (TrainerCareer career) {
        this.careerId = career.getId();
        this.company = career.getCompany();
        this.address = career.getAddress();
        this.startDate = career.getStartDate();
        this.endDate = career.getEndDate() != null ? career.getEndDate() : null;
        this.work = career.getWork();
        this.working = career.isWorking();
    }
}
