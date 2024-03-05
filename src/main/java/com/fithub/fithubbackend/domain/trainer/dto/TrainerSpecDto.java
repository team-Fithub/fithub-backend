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
    private String address;

    @Builder
    public TrainerSpecDto(List<TrainerCareer> trainerCareerList, List<TrainerLicenseImg> trainerLicenseList, String address) {
        this.trainerCareerList = trainerCareerList.stream().map(c -> TrainerCareerDto.builder().career(c).build()).toList();
        this.trainerLicenseList = trainerLicenseList.stream().map(l -> TrainerLicenseDto.builder().licenseImg(l).build()).toList();
        this.address = address;
    }
}
