package com.fithub.fithubbackend.domain.Training.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location {

    @NotNull
    @Schema(description = "위도", example = "37.573319")
    private Double latitude;

    @NotNull
    @Schema(description = "경도", example = "126.915444")
    private Double longitude;
}
