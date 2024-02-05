package com.fithub.fithubbackend.domain.Training.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fithub.fithubbackend.domain.Training.enums.ReserveStatus;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "회원의 트레이닝 예약, 진행,종료 정보 확인")
public class UsersReserveOutlineDto {

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

    @Schema(description = "트레이닝 진행 상황(진행 전, 진행중, 진행완료)")
    private ReserveStatus status;

    @Schema(description = "트레이닝 결제(예약)한 날짜, 시간")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentDateTime;

    @Schema(description = "트레이닝 결제 변경 날짜, 시간 (취소, 노쇼 처리 시 이걸 참고)")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDateTime;

    @QueryProjection
    public UsersReserveOutlineDto(Long reservationId, Long trainingId, String title, String location,
                                  LocalDateTime reserveDateTime, ReserveStatus status,
                                  LocalDateTime paymentDateTime, LocalDateTime modifiedDateTime) {
        this.reservationId = reservationId;
        this.trainingId = trainingId;
        this.title = title;
        this.location = location;
        this.reserveDateTime = reserveDateTime;
        this.status = status;
        this.paymentDateTime = paymentDateTime;
        this.modifiedDateTime = modifiedDateTime;
    }
}
