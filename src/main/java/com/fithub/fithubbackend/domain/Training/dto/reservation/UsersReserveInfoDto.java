package com.fithub.fithubbackend.domain.Training.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fithub.fithubbackend.domain.Training.domain.ReserveInfo;
import com.fithub.fithubbackend.domain.Training.domain.Training;
import com.fithub.fithubbackend.domain.Training.enums.ReserveStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UsersReserveInfoDto {

    @Schema(description = "트레이닝 예약 id")
    private Long reservationId;

    @Schema(description = "트레이닝 id")
    private Long trainingId;

    @Schema(description = "예약한 트레이닝의 트레이너 프로필 이미지")
    private String trainerProfileImgUrl;

    @Schema(description = "트레이닝 제목")
    private String title;

    @Schema(description = "예약한 트레이닝 날짜, 시간")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reserveDateTime;

    private String address;

    private int price;

    @Schema(description = "트레이닝 결제(주문) 번호")
    private String impUid;

    @Schema(description = "트레이닝 진행 상황(진행 전, 진행중, 진행완료)")
    private ReserveStatus status;

    @Schema(description = "트레이닝 결제(예약)한 날짜, 시간")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentDateTime;

    @Schema(description = "트레이닝 결제 변경 날짜, 시간 (취소, 노쇼 처리 시 이걸 참고)")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDateTime;

    @Builder
    public UsersReserveInfoDto(ReserveInfo reserveInfo, Training training) {
        this.reservationId = reserveInfo.getId();
        this.trainingId = training.getId();
        this.trainerProfileImgUrl = training.getTrainer().getProfileUrl();
        this.title = training.getTitle();
        this.address = training.getAddress();
        this.reserveDateTime = reserveInfo.getReserveDateTime();
        this.price = reserveInfo.getPrice();
        this.impUid = reserveInfo.getImpUid();
        this.status = reserveInfo.getStatus();
        this.paymentDateTime = reserveInfo.getCreatedDate();
        this.modifiedDateTime = reserveInfo.getModifiedDate();
    }
}
