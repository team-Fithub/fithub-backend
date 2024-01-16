package com.fithub.fithubbackend.domain.Training.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fithub.fithubbackend.domain.Training.domain.AvailableTime;
import lombok.*;

import java.time.LocalTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TrainingAvailableTimeDto {

    private Long id;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime time;

    private boolean enabled;

    public static TrainingAvailableTimeDto toDto(AvailableTime time) {
        return TrainingAvailableTimeDto.builder()
                .id(time.getId())
                .time(time.getTime())
                .enabled(time.isEnabled())
                .build();
    }
}
