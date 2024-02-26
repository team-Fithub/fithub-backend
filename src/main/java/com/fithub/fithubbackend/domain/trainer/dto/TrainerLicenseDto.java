package com.fithub.fithubbackend.domain.trainer.dto;

import com.fithub.fithubbackend.domain.trainer.domain.TrainerLicenseImg;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainerLicenseDto {
    private Long licenseId;

    @NotNull
    private String url;

    @NotNull
    private String inputName;

    @Builder
    public TrainerLicenseDto (TrainerLicenseImg licenseImg) {
        this.licenseId = licenseImg.getId();
        this.url = licenseImg.getDocument().getUrl();
        this.inputName = licenseImg.getDocument().getInputName();
    }

}
