package com.fithub.fithubbackend.domain.trainer.dto;

import com.fithub.fithubbackend.global.common.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainerRecommendationOutlineDto {

    @Schema(description = "회원의 관심사")
    private Category interest;

    @Schema(description = "트레이너 추천 리스트")
    private List<TrainerRecommendationDto> trainerRecommendationList;

    @Builder
    public TrainerRecommendationOutlineDto(Category interest, List<TrainerRecommendationDto> trainerRecommendationList) {
        this.interest = interest;
        this.trainerRecommendationList = trainerRecommendationList;
    }

}
