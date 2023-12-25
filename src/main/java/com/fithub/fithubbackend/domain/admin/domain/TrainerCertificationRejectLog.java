package com.fithub.fithubbackend.domain.admin.domain;

import com.fithub.fithubbackend.domain.admin.dto.CertRejectDto;
import com.fithub.fithubbackend.domain.admin.enums.RejectType;
import com.fithub.fithubbackend.domain.trainer.domain.TrainerCertificationRequest;
import com.fithub.fithubbackend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainerCertificationRejectLog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, cascade = CascadeType.REMOVE)
    private TrainerCertificationRequest trainerCertificationRequest;

    @Enumerated(EnumType.STRING)
    @NotNull
    private RejectType rejectType;

    private String reason;

    @Builder
    public TrainerCertificationRejectLog(TrainerCertificationRequest trainerCertificationRequest, CertRejectDto dto) {
        this.trainerCertificationRequest = trainerCertificationRequest;
        this.rejectType = dto.getRejectType();
        this.reason = dto.getReason() != null ? dto.getReason() : null;
    }
}
