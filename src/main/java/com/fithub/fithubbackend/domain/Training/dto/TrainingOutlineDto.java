package com.fithub.fithubbackend.domain.Training.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fithub.fithubbackend.domain.Training.domain.Training;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class TrainingOutlineDto {
    private Long id;
    private TrainerInfoDto trainerInfoDto;
    private String title;
    private int price;
    private String address;

    @Schema(description = "보내준 위치와의 거리. 위치로 트레이닝 검색할 때만 값이 들어가있음")
    private Double dist;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private boolean closed;

    public static TrainingOutlineDto toDto(Training training) {
        return TrainingOutlineDto.builder()
                .id(training.getId())
                .trainerInfoDto(TrainerInfoDto.toDto(training.getTrainer()))
                .title(training.getTitle())
                .price(training.getPrice())
                .address(training.getAddress())
                .startDate(training.getStartDate())
                .endDate(training.getEndDate())
                .closed(training.isClosed())
                .build();
    }

    public void updateDist(Double dist) {
        this.dist = dist;
    }
}
