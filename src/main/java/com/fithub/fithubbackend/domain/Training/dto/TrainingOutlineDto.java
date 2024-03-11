package com.fithub.fithubbackend.domain.Training.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fithub.fithubbackend.domain.Training.domain.Training;
import com.fithub.fithubbackend.domain.Training.domain.TrainingCategory;
import com.fithub.fithubbackend.global.common.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
    private List<Category> categories;

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
                .categories(training.getCategories().stream().map(TrainingCategory::getCategory).collect(Collectors.toList()))
                .build();
    }

    public void updateDist(Double dist) {
        this.dist = dist;
    }
}
