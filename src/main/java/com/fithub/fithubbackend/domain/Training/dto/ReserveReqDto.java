package com.fithub.fithubbackend.domain.Training.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "트레이닝 결제 검증 후 정보 저장을 위한 dto")
public class ReserveReqDto {
    @NotNull
    @Schema(description = "예약할 트레이닝의 id")
    private Long trainingId;

    @NotNull
    @Schema(description = "상점 id kc는 html5_inicis 아니면 inicis")
    private String pg;

    @NotNull
    @Schema(description = "결제 방법")
    private String payMethod;

    @NotNull
    @Schema(description = "포트원 거래고유번호")
    private String impUid;

    @NotNull
    @Schema(description = "주문 번호")
    private String merchantUid;

    @NotNull
    @Schema(description = "가격")
    private int amount;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "예약한 날짜, 시간")
    private LocalDateTime reserveDateTime;
}
