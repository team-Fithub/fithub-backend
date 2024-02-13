package com.fithub.fithubbackend.domain.Training.dto.reservation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "트레이닝 예약 결제 금액 검증을 위한 dto")
public class PaymentReqDto {
    @NotNull
    @Schema(description = "예약 id")
    private Long reservationId;

    @NotNull
    @Schema(description = "결제 방법")
    private String payMethod;

    @NotNull
    @Schema(description = "포트원 거래고유번호")
    private String impUid;

    @NotNull
    @Schema(description = "주문 번호")
    private String merchantUid;

}
