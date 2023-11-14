package com.fithub.fithubbackend.domain.Training.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fithub.fithubbackend.domain.Training.domain.AvailableDate;
import com.fithub.fithubbackend.domain.Training.domain.Training;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class TrainingInfoDto {

    private Long id;

    private TrainerInfoDto trainerInfoDto;

    private String title;
    private String content;
    private String location;

    private int quota;

    private int price;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private List<AvailableDate> availableDates;

    public static TrainingInfoDto toDto(Training training) {
        return TrainingInfoDto.builder()
                .id(training.getId())
                .trainerInfoDto(TrainerInfoDto.toDto(training.getTrainer()))
                .title(training.getTitle())
                .content(training.getContent())
                .location(training.getLocation())
                .quota(training.getQuota())
                .price(training.getPrice())
                .startDate(training.getStartDate())
                .endDate(training.getEndDate())
                .availableDates(training.getAvailableDates())
                .build();
    }
}
