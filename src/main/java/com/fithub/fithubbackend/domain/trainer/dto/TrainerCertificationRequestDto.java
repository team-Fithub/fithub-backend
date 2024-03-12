package com.fithub.fithubbackend.domain.trainer.dto;

import com.fithub.fithubbackend.global.common.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@Schema(description = "트레이너 인증 요청 dto")
public class TrainerCertificationRequestDto {
    @Valid
    @NotNull(message = "자격증은 1개 이상 등록되어야 합니다.")
    @Schema(description = "트레이너 인증 요청 시 첨부한 자격증 파일 리스트")
    private List<MultipartFile> licenseFileList;

    @NotBlank
    @Schema(description = "트레이너 인증 요청 시 입력한 자격증 리스트", example = "2급 생활스포츠지도사,NSCA")
    private String licenseNames;

    @Valid
    @NotNull(message = "경력사항은 1개 이상 등록되어야 합니다.")
    @Schema(description = "트레이너 인증 요청 시 작성한 경력사항")
    private List<TrainerCareerRequestDto> careerList;

}
