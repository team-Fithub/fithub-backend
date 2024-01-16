package com.fithub.fithubbackend.domain.Training.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fithub.fithubbackend.domain.Training.domain.AvailableDate;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TrainingAvailableDateDto {
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private boolean enabled;

    private List<TrainingAvailableTimeDto> availableTimes = new ArrayList<>();

    public static TrainingAvailableDateDto toDto(AvailableDate date) {
        return TrainingAvailableDateDto.builder()
                .id(date.getId())
                .date(date.getDate())
                .enabled(date.isEnabled())
                .availableTimes(date.getAvailableTimes().stream().map(TrainingAvailableTimeDto::toDto).toList())
                .build();
    }

}
