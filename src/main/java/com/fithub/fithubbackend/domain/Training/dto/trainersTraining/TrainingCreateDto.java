package com.fithub.fithubbackend.domain.Training.dto.trainersTraining;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Schema(description = "트레이닝 생성 dto")
public class TrainingCreateDto {

    @NotBlank
    @Size(min = 2, max = 100)
    @Schema(description = "트레이닝 제목")
    private String title;

    @NotBlank
    @Schema(description = "트레이닝 소개글")
    private String content;

    private List<MultipartFile> images = new ArrayList<>();

    @Schema(description = "가격")
    private int price;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "트레이닝 시작 날짜")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "트레이닝 마감 날짜")
    private LocalDate endDate;

    @Schema(
            description = "트레이닝 예약 가능한 첫번째 시간",
            type="string",
            example = "11:00:00",
            pattern="HH:mm:ss"
    )
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startHour;

    @Schema(
            description = "트레이닝 예약 가능한 마지막 시간",
            type="string",
            example = "11:00:00",
            pattern="HH:mm:ss"
    )
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endHour;

    @Schema(description = "트레이닝 예약 날짜에서 트레이너가 안 된다고 체크한 날짜, 없으면 안 주면 됨")
    private List<LocalDate> unableDates = new ArrayList<>();

    public boolean isDateInvalid(LocalDate startDate, LocalDate endDate) {
        LocalDate now = LocalDate.now();
        return startDate.isBefore(now) || endDate.isBefore(now) || endDate.isBefore(startDate);
    }
}
