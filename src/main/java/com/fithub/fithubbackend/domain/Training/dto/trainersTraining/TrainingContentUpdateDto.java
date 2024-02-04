package com.fithub.fithubbackend.domain.Training.dto.trainersTraining;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainingContentUpdateDto {
    @NotNull
    private String title;

    @NotNull
    private String content;

    @NotNull
    private int price;

    @NotNull
    private int quota;

    @Valid
    private TrainingImgUpdateDto trainingImgUpdateDto;
}
