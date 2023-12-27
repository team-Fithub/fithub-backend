package com.fithub.fithubbackend.domain.admin.dto;

import com.fithub.fithubbackend.domain.admin.enums.RejectType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "트레이너 인증 요청을 거절하는 이유")
public class CertRejectDto {
    @NotNull
    @Schema(description = "거절 타입")
    private RejectType rejectType;
    
    @Schema(description = "거절하는 상세한 설명이 필요하다면 작성")
    private String reason;
}
