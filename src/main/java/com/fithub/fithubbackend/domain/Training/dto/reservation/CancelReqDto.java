package com.fithub.fithubbackend.domain.Training.dto.reservation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "결제 취소 dto")
public class CancelReqDto {
    @NotNull
    @Schema(description = "결제했던 트레이닝 id")
    private Long trainingId;

    @NotNull
    @Schema(description = "결제 내역 id")
    private Long reservationId;

    @NotNull
    @Schema(description = "포트원 거래고유번호")
    private String impUid;

    @NotNull
    @Schema(description = "결제했던 금액(환불 금액)")
    private String amount;
}
