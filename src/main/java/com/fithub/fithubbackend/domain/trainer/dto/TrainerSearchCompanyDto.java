package com.fithub.fithubbackend.domain.trainer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainerSearchCompanyDto {

    @Schema(description = "근무 중인 회사명")
    private String company;

    @Schema(description = "위도", example = "37.573319")
    private Double latitude;

    @Schema(description = "경도", example = "126.915444")
    private Double longitude;

    @Builder
    public TrainerSearchCompanyDto(String company, Double latitude, Double longitude) {
        this.company = company;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
