package com.fithub.fithubbackend.domain.Training.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fithub.fithubbackend.domain.Training.domain.ReserveInfo;
import com.fithub.fithubbackend.domain.Training.domain.Training;
import com.fithub.fithubbackend.domain.Training.enums.ReserveStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Schema(description = "회원의 트레이닝 종료 내역 확인 dto")
public class UsersReserveCompleteOutlineDto {
    @Schema(description = "트레이닝 예약 id")
    private Long reservationId;

    @Schema(description = "트레이닝 id")
    private Long trainingId;

    @Schema(description = "트레이닝 제목")
    private String title;

    @Schema(description = "예약한 트레이닝 날짜, 시간")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reserveDateTime;

    private String location;

    @Schema(description = "트레이닝 진행 상황(진행완료)")
    private ReserveStatus status;

    @Schema(description = "트레이닝 리뷰 작성 여부")
    private boolean reviewWritten;

    @Schema(description = "트레이닝 결제(예약)한 날짜, 시간")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentDateTime;

    @Schema(description = "트레이닝 결제 변경 날짜, 시간 (취소, 노쇼 처리 시 이걸 참고)")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDateTime;

    @Builder
    public UsersReserveCompleteOutlineDto(ReserveInfo reserveInfo, boolean reviewWritten) {
        Training training = reserveInfo.getTraining();

        this.reservationId = reserveInfo.getId();
        this.trainingId = training.getId();
        this.title = training.getTitle();
        this.reserveDateTime = reserveInfo.getReserveDateTime();
        this.location = training.getAddress();
        this.status = reserveInfo.getStatus();
        this.reviewWritten = reviewWritten;
        this.paymentDateTime = reserveInfo.getCreatedDate();
        this.modifiedDateTime = reserveInfo.getModifiedDate();
    }

}
