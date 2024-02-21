package com.fithub.fithubbackend.domain.trainer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "트레이너 인증 요청 시 작성하는 경력 사항")
public class TrainerCareerRequestDto {
    @NotBlank
    @Schema(description = "회사명")
    private String company;

    @NotNull
    @Schema(description = "회사 주소")
    private String address;

    @Schema(description = "경도")
    private Double longitude;

    @Schema(description = "위도")
    private Double latitude;
    
    @NotBlank
    @Schema(description = "담당 업무")
    private String work;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "입사년월")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "퇴사년월")
    private LocalDate endDate;

    @NotNull
    @Schema(description = "현 근무지 재직 여부")
    private boolean working;
}
