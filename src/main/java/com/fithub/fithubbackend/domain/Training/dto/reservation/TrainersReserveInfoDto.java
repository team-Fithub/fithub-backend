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
@Schema(description = "트레이너의 트레이닝 예약 정보 확인")
public class TrainersReserveInfoDto {
    @Schema(description = "트레이닝 id")
    private Long id;

    @Schema(description = "트레이닝 제목")
    private String title;

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
    
   public static TrainersReserveInfoDto toDto(ReserveInfo reserveInfo) {
            return TrainersReserveInfoDto.builder()
                    .id(reserveInfo.getId())
                    .title(reserveInfo.getTraining().getTitle())
                    .userName(reserveInfo.getUser().getName())
                    .trainingDateTime(reserveInfo.getReserveDateTime())
                    .createdDateTime(reserveInfo.getCreatedDate())
                    .status(reserveInfo.getStatus())
                    .price(reserveInfo.getPrice())
                    .build();
    }
}
