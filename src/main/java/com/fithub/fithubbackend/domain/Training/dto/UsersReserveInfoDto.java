package com.fithub.fithubbackend.domain.Training.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fithub.fithubbackend.domain.Training.domain.ReserveInfo;
import com.fithub.fithubbackend.domain.Training.enums.ReserveStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "회원의 트레이닝 예약 정보 확인")
public class UsersReserveInfoDto {
    @Schema(description = "트레이닝 id")
    private Long id;

    @Schema(description = "트레이닝 제목")
    private String title;

    @Schema(description = "트레이닝을 담당하는 트레이너 이름")
    private String trainerName;

    @Schema(description = "예약한 트레이닝 날짜, 시간")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime trainingDateTime;

    @Schema(description = "트레이닝을 예약한 날짜, 시간")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reserveDateTime;

    @Schema(description = "트레이닝 진행 상황(진행 전, 진행중, 진행완료, 취소)")
    private ReserveStatus status;

    @Schema(description = "트레이닝 결제 금액")
    private int price;

    @Schema(description = "트레이닝 구매 번호")
    private String merchantUid;

    // TODO: 트레이너용 dto, 회원용 dto로 나누기 (트레이너한테는 자기 이름 필요없으니까 불필요한 trainer 조회 없이가도록)
    public static UsersReserveInfoDto toDto(ReserveInfo reserveInfo) {
        return UsersReserveInfoDto.builder()
                .id(reserveInfo.getId())
                .title(reserveInfo.getTraining().getTitle())
                .trainerName(reserveInfo.getTrainer().getName())
                .trainingDateTime(reserveInfo.getReserveDateTime())
                .reserveDateTime(reserveInfo.getCreatedDate())
                .status(reserveInfo.getStatus())
                .price(reserveInfo.getPrice())
                .merchantUid(reserveInfo.getMerchantUid())
                .build();
    }
}
