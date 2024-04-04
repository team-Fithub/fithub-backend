package com.fithub.fithubbackend.domain.trainer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainerSearchAllLicenseDto {

    @Schema(description = "자격 사항 수")
    private int totalCounts;

    @Schema(description = "자격 사항 정보")
    List<TrainerLicenseDto> licenses;

    @Builder
    public TrainerSearchAllLicenseDto(int totalCounts, List<TrainerLicenseDto> licenses) {
        this.totalCounts = totalCounts;
        this.licenses = licenses;
    }
}
