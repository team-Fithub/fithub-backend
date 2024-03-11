package com.fithub.fithubbackend.domain.Training.dto.trainersTraining;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "트레이닝 내용 수정 요청 Dto")
public class TrainingContentUpdateDto {
    @NotNull
    private String title;

    @NotNull
    private String content;

    @NotNull
    private int price;

    @Schema(description = "트레이닝 이미지를 수정했다면 값을 넣어주면 됨. 수정 없으면 안 줘야 됨")
    private TrainingImgUpdateDto trainingImgUpdateDto;

    @Schema(description = "트레이닝 카테고리를 수정했다면 값을 넣어주면 됨. 수정 없으면 안 줘야 됨")
    private TrainingCategoryUpdateDto trainingCategoryUpdateDto;
}
