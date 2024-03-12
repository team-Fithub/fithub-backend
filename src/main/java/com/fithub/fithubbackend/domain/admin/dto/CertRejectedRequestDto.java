package com.fithub.fithubbackend.domain.admin.dto;

import com.fithub.fithubbackend.domain.admin.domain.TrainerCertificationRejectLog;
import com.fithub.fithubbackend.domain.admin.enums.RejectType;
import com.fithub.fithubbackend.domain.trainer.domain.TrainerCertificationRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CertRejectedRequestDto {

    private Long rejectLogId;
    private Long requestId;

    private String userName;
    private String userEmail;

    private RejectType rejectType;
    private String reason;

    private LocalDateTime requestDateTime;
    private LocalDateTime rejectedDateTime;

    @Builder
    public CertRejectedRequestDto(TrainerCertificationRejectLog log, TrainerCertificationRequest request) {
        this.rejectLogId = log.getId();
        this.requestId = request.getId();
        this.userName= request.getUser().getName();
        this.userEmail = request.getUser().getEmail();
        this.rejectType = log.getRejectType();
        this.reason = log.getReason();
        this.requestDateTime = request.getCreatedDate();
        this.rejectedDateTime = log.getCreatedDate();
    }
}
