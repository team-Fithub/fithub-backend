package com.fithub.fithubbackend.domain.trainer.dto;

import com.fithub.fithubbackend.domain.trainer.domain.TrainerCareer;
import com.fithub.fithubbackend.domain.trainer.domain.TrainerExpertise;
import com.fithub.fithubbackend.domain.trainer.domain.TrainerLicenseImg;
import com.fithub.fithubbackend.global.common.Category;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "트레이너 전문분야")
    private List<Category> trainerExpertiseList;
    private String address;

    @Builder
    public TrainerSpecDto(List<TrainerCareer> trainerCareerList, List<TrainerLicenseImg> trainerLicenseList, List<TrainerExpertise> trainerExpertiseList, String address) {
        this.trainerCareerList = trainerCareerList.stream().map(c -> TrainerCareerDto.builder().career(c).build()).toList();
        this.trainerLicenseList = trainerLicenseList.stream().map(l -> TrainerLicenseDto.builder().licenseImg(l).build()).toList();
        this.trainerExpertiseList = trainerExpertiseList.stream().map(TrainerExpertise::getExpertise).toList();
        this.address = address;
    }
}
