package com.fithub.fithubbackend.domain.Training.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fithub.fithubbackend.domain.Training.enums.ReserveStatus;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Schema(description = "트레이너의 트레이닝 예약 정보 확인")
public class TrainersReserveInfoDto {
    @Schema(description = "트레이닝 id")
    private Long trainingId;

    @Schema(description = "트레이닝 제목")
    private String title;

    @Schema(description = "트레이닝 예약자 primary key id")
    private Long userId;

    @Schema(description = "트레이닝 예약자 이름")
    private String userName;

    @Schema(description = "예약한 트레이닝 날짜, 시간")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime trainingDateTime;
    
    @Schema(description = "트레이닝을 예약한 날짜, 시간")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDateTime;
    
    @Schema(description = "트레이닝 진행 상황(진행 전, 진행중, 진행완료, 취소)")
    private ReserveStatus status;

    @Schema(description = "트레이닝 결제 금액")
    private int price;

    @QueryProjection
    public TrainersReserveInfoDto(Long trainingId, String title, Long userId, String userName,
                                  ReserveStatus status, int price,
                                  LocalDateTime trainingDateTime, LocalDateTime createdDateTime) {
        this.trainingId = trainingId;
        this.title = title;
        this.userId = userId;
        this.userName = userName;
        this.trainingDateTime = trainingDateTime;
        this.status = status;
        this.price = price;
        this.createdDateTime = createdDateTime;
    }
}
