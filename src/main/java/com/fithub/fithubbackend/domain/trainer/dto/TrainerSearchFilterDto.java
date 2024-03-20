package com.fithub.fithubbackend.domain.trainer.dto;

import com.fithub.fithubbackend.domain.user.enums.Gender;
import com.fithub.fithubbackend.global.common.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainerSearchFilterDto {

    @Schema(description = "관심사(PILATES, HEALTH, PT, CROSSFIT, YOGA). 여러 개 선택 불가능", example = "PILATES")
    private Category interest;

    @Schema(description = "검색 키워드(트레이너 이름)", example = "김핏헙")
    private String keyword;

    @Schema(description = "성별 (F, M)", example = "F")
    private Gender gender;

}
