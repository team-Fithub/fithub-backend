package com.fithub.fithubbackend.domain.Training.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class TrainingCreateDto {

    @NotBlank
    @Size(min = 2, max = 100)
    private String title;
    @NotBlank
    private String content;
    @NotBlank
    private String location;

    @Min(1)
    private int quota;

    private int price;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startHour;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endHour;

    private List<LocalDate> unableDates;

    public boolean isDateInvalid(LocalDate startDate, LocalDate endDate) {
        LocalDate now = LocalDate.now();
        return startDate.isBefore(now) || endDate.isBefore(now) || endDate.isBefore(startDate);
    }
}
