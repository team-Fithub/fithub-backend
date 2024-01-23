package com.fithub.fithubbackend.domain.Training.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "트레이닝 리뷰 작성 dto")
public class TrainingReviewReqDto {

    @NotNull
    @Schema(description = "트레이닝 리뷰 작성을 위한 예약 내역의 id")
    private Long reservationId;

    @NotNull
    @Schema(description = "리뷰 작성 내용")
    private String content;

    @NotNull
    @Schema(description = "회원이 주는 트레이닝 별점")
    private int star;
}
