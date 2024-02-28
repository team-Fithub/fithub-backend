 package com.fithub.fithubbackend.domain.Training.dto.trainersTraining;

 import io.swagger.v3.oas.annotations.media.Schema;
 import jakarta.validation.constraints.NotNull;
 import lombok.Getter;
 import lombok.Setter;

 import java.time.LocalDate;
 import java.util.ArrayList;
 import java.util.List;

@Getter
@Setter
public class TrainingDateUpdateDto {

    @NotNull
    @Schema(description = "트레이닝 시작 날짜, 달라지지 않았으면 기존의 날짜로")
    private LocalDate startDate;
    
    @NotNull
    @Schema(description = "트레이닝 마지막 날짜, 달라지지 않았으면 기존의 날짜로")
    private LocalDate endDate;

    @Schema(description = "트레이닝 불가 날짜", example = "[\"2024-03-02\",\"2024-03-05\"]")
    private List<LocalDate> unableDates = new ArrayList<>();
}
