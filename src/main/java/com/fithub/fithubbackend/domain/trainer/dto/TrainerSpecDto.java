package com.fithub.fithubbackend.domain.trainer.dto;

import com.fithub.fithubbackend.domain.trainer.domain.TrainerCareer;
import com.fithub.fithubbackend.domain.trainer.domain.TrainerLicenseImg;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainerSpecDto {
    private List<TrainerCareerDto> trainerCareerList;
    private List<TrainerLicenseDto> trainerLicenseList;

    @Builder
    public TrainerSpecDto(List<TrainerCareer> trainerCareerList, List<TrainerLicenseImg> trainerLicenseList) {
        this.trainerCareerList = trainerCareerList.stream().map(c -> TrainerCareerDto.builder().career(c).build()).toList();
        this.trainerLicenseList = trainerLicenseList.stream().map(l -> TrainerLicenseDto.builder().licenseImg(l).build()).toList();
    }
}
