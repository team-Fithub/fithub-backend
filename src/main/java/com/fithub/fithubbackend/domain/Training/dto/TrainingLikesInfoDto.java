package com.fithub.fithubbackend.domain.Training.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "트레이닝 찜 리스트 조회 dto")
public class TrainingLikesInfoDto {
    @Schema(description = "트레이닝 찜 primary key")
    private Long id;

    @Schema(description = "트레이닝 요약 정보 dto (트레이너 정보 포함)")
    private TrainingOutlineDto trainingOutlineDto;
}
