package com.fithub.fithubbackend.domain.user.domain;

import com.fithub.fithubbackend.domain.user.enums.ClosureReasonType;
import com.fithub.fithubbackend.global.common.BaseTimeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClosureReason extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Schema(description = "탈퇴 사유")
    @Enumerated(EnumType.STRING)
    private ClosureReasonType reasonType;

    @Schema(description = "회원이 직접 입력한 탈퇴 사유")
    private String customReason;

    @Builder
    public ClosureReason (ClosureReasonType reasonType, String customReason) {
        this.reasonType = reasonType;
        this.customReason = customReason;
    }
}