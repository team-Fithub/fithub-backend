package com.fithub.fithubbackend.domain.admin.dto;

import com.fithub.fithubbackend.domain.trainer.domain.TrainerCertificationRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "관리자 - 트레이너 인증 요청 전체 조회 시 사용하는 요약 정보")
public class CertRequestOutlineDto {
    private Long requestId;
    private String userName;
    private String userEmail;
    private LocalDateTime requestDateTime;

    @Builder
    public CertRequestOutlineDto(TrainerCertificationRequest request) {
        this.requestId = request.getId();
        this.userName= request.getUser().getName();
        this.userEmail = request.getUser().getEmail();
        this.requestDateTime = request.getCreatedDate();
    }
}
