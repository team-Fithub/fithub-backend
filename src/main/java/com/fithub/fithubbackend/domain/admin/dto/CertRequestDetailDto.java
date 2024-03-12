package com.fithub.fithubbackend.domain.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Schema(description = "관리자 - 트레이너 인증 요청 하나 조회 시 사용하는 상세 정보")
public class CertRequestDetailDto {

    private Long id;

    @Schema(description = "인증 요청 신청자 정보")
    private ApplicantInfoDto applicantInfoDto;

    @Schema(description = "인증 요성 시 넣은 자격증 리스트")
    private List<TrainerLicenseTempImgDto> licenseTempImgList;

    @Schema(description = "인증 요청 시 넣은 경력 리스트")
    private List<TrainerCareerTempDto> careerTempList;
}
