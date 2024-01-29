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
@Schema(description = "회원의 트레이닝 예약, 진행,종료 정보 확인")
public class UsersReserveInfoDto {

    @Schema(description = "트레이닝 예약 id")
    private Long reservationId;

    @Schema(description = "트레이닝 id")
    private Long trainingId;

    @Schema(description = "트레이닝 제목")
    private String title;

    @Schema(description = "예약한 트레이닝 날짜, 시간")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reserveDateTime;

    @Schema(description = "트레이닝 진행 상황(진행 전, 진행중, 진행완료)")
    private ReserveStatus status;

    @Schema(description = "트레이닝 결제 금액")
    private int price;

    @Schema(description = "트레이닝 구매 번호")
    private String merchantUid;

    @Schema(description = "트레이닝 결제(예약)한 날짜, 시간")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentDateTime;

    public static UsersReserveInfoDto toDto(ReserveInfo reserveInfo) {
        return UsersReserveInfoDto.builder()
                .reservationId(reserveInfo.getId())
                .trainingId(reserveInfo.getTraining().getId())
                .title(reserveInfo.getTraining().getTitle())
                .reserveDateTime(reserveInfo.getReserveDateTime())
                .status(reserveInfo.getStatus())
                .price(reserveInfo.getPrice())
                .merchantUid(reserveInfo.getMerchantUid())
                .paymentDateTime(reserveInfo.getCreatedDate())
                .build();
    }
}
