package com.fithub.fithubbackend.domain.Training.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "트레이닝 결제 금액 검증 전 예약 시간대 확인 및 저장을 위한 dto")
public class ReserveReqDto {
    @NotNull
    @Schema(description = "예약할 트레이닝의 id")
    private Long trainingId;

    @NotNull
    @Schema(description = "가격")
    private int amount;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "예약한 날짜, 시간")
    private LocalDateTime reserveDateTime;
}
