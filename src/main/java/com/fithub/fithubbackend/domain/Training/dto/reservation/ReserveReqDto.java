package com.fithub.fithubbackend.domain.Training.dto.reservation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "트레이닝 결제 금액 검증 전 예약 시간대 확인 및 저장을 위한 dto")
public class ReserveReqDto {
    @NotNull
    @Schema(description = "예약할 트레이닝의 id")
    private Long trainingId;

    @NotNull
    @Schema(description = "예약한 날짜 id")
    private Long reservationDateId;

    @NotNull
    @Schema(description = "예약한 시간 id")
    private Long reservationTimeId;
}
