package com.fithub.fithubbackend.domain.Training.dto.reservation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Schema(description = "결제 완료 후 보내주는 응답 데이터, 결제 정보가 들어있음")
public class PaymentResDto {
    private String impUid;
    private String merchantUid;

    @Schema(description = "현재 카드 결제만 되므로 card라고만 있을 것")
    private String payMethod;

    @Schema(description = "결제 금액")
    private int amount;

    @Schema(description = "트레이닝 id")
    private Long trainingId;

    @Schema(description = "트레이닝 제목")
    private String title;

    @Schema(description = "트레이닝 예약 시간")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime reservationDateTime;

    @Schema(description = "결제 진행된 시간")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime paymentDateTime;

}
