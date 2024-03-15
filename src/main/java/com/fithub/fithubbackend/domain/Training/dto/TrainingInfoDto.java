package com.fithub.fithubbackend.domain.Training.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fithub.fithubbackend.domain.Training.domain.Training;
import com.fithub.fithubbackend.domain.Training.domain.TrainingCategory;
import com.fithub.fithubbackend.global.common.Category;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class TrainingInfoDto {

    private Long id;

    private TrainerInfoDto trainerInfoDto;

    private String title;
    private String content;
    private String address;

    private List<TrainingDocumentDto> images;

    private int price;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private List<TrainingAvailableDateDto> availableDates;

    private List<Category> categories;

    public static TrainingInfoDto toDto(Training training) {
        return TrainingInfoDto.builder()
                .id(training.getId())
                .trainerInfoDto(TrainerInfoDto.toDto(training.getTrainer()))
                .title(training.getTitle())
                .content(training.getContent())
                .address(training.getAddress())
                .price(training.getPrice())
                .startDate(training.getStartDate())
                .endDate(training.getEndDate())
                .availableDates(training.getAvailableDates().stream().map(TrainingAvailableDateDto::toDto).toList())
                .categories(training.getCategories().stream().map(TrainingCategory::getCategory).collect(Collectors.toList()))
                .build();
    }

    public void updateImages(List<TrainingDocumentDto> images) {
        this.images = images;
    }
}
