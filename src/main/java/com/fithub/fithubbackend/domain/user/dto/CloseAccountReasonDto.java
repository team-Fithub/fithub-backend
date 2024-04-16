package com.fithub.fithubbackend.domain.user.dto;

import com.fithub.fithubbackend.domain.user.enums.ClosureReasonType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CloseAccountReasonDto {

    @NotNull
    @Schema(description = "탈퇴 사유")
    @Enumerated(EnumType.STRING)
    private ClosureReasonType closureReasonType;

    @Schema(description = "회원이 직접 입력한 탈퇴 사유. closureReasonType 이 \"OTHER\" 일 경우에만 보내줌.")
    private String customReason;
}