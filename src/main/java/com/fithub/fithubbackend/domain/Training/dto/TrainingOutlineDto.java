package com.fithub.fithubbackend.domain.Training.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fithub.fithubbackend.domain.Training.domain.Training;
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
    private String location;

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
                .location(training.getLocation())
                .startDate(training.getStartDate())
                .endDate(training.getEndDate())
                .closed(training.isClosed())
                .build();
    }
}
