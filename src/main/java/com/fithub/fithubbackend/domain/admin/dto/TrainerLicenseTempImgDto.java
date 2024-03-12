package com.fithub.fithubbackend.domain.admin.dto;

import com.fithub.fithubbackend.domain.trainer.domain.TrainerLicenseTempImg;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainerLicenseTempImgDto {
    private String url;
    private String inputName;

    @Builder
    public TrainerLicenseTempImgDto(TrainerLicenseTempImg tempImg) {
        this.url = tempImg.getDocument().getUrl();
        this.inputName = tempImg.getDocument().getInputName();
    }
}
