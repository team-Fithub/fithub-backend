package com.fithub.fithubbackend.domain.Training.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fithub.fithubbackend.domain.Training.domain.ReserveInfo;
import com.fithub.fithubbackend.domain.Training.enums.ReserveStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "회원의 트레이닝 예약 취소 정보 확인")
public class UsersReserveCancelInfoDto {

    @Schema(description = "트레이닝 예약 id")
    private Long reservationId;

    @Schema(description = "트레이닝 id")
    private Long trainingId;

    @Schema(description = "트레이닝 제목")
    private String title;

    @Schema(description = "예약한 트레이닝 날짜, 시간")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reserveDateTime;

    @Schema(description = "트레이닝 진행 상황(취소, 노쇼)")
    private ReserveStatus status;

    @Schema(description = "트레이닝 결제 금액")
    private int price;

    @Schema(description = "트레이닝 구매 번호")
    private String merchantUid;

    @Schema(description = "트레이닝을 취소한 날짜, 시간")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime cancelDateTime;

    public static UsersReserveCancelInfoDto toDto(ReserveInfo reserveInfo) {
        return UsersReserveCancelInfoDto.builder()
                .reservationId(reserveInfo.getId())
                .trainingId(reserveInfo.getTraining().getId())
                .title(reserveInfo.getTraining().getTitle())
                .reserveDateTime(reserveInfo.getReserveDateTime())
                .status(reserveInfo.getStatus())
                .price(reserveInfo.getPrice())
                .merchantUid(reserveInfo.getMerchantUid())
                .cancelDateTime(reserveInfo.getModifiedDate())
                .build();
    }
}
