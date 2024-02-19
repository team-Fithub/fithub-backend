package com.fithub.fithubbackend.domain.Training.dto.trainersTraining;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class TrainingTimeUpdateDto {
    @NotNull
    private LocalTime startTime;
    @NotNull
    private LocalTime endTime;
}
