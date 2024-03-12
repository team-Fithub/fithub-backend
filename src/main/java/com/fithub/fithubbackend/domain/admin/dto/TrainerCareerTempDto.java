package com.fithub.fithubbackend.domain.admin.dto;

import com.fithub.fithubbackend.domain.trainer.domain.TrainerCareerTemp;
import lombok.*;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainerCareerTempDto {
    private String company;
    private String address;
    private String work;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean working;

    @Builder
    public TrainerCareerTempDto(TrainerCareerTemp temp) {
        this.company = temp.getCompany();
        this.address = temp.getAddress();
        this.work = temp.getWork();
        this.startDate = temp.getStartDate();
        this.endDate = temp.getEndDate();
        this.working = temp.isWorking();
    }
}
