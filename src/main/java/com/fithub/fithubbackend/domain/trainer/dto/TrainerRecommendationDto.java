package com.fithub.fithubbackend.domain.trainer.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fithub.fithubbackend.global.common.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Setter @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "트레이너 추천 dto")
public class TrainerRecommendationDto {

    @Schema(description = "현재 일하는 장소")
    private String address;

    @Schema(description = "트레이너 id")
    private long trainerId;

    @JsonIgnore
    private long userId;

    @Schema(description = "트레이너 이름")
    private String name;

    @Schema(description = "트레이너 프로필 이미지")
    private String profileUrl;

    @Schema(description = "트레이너 전문 분야")
    private List<Category> interests;

    @Schema(description = "트레이너 평점")
    private double rating;

    @Schema(description = "트레이너 후기 수")
    private long totalReviews;

    @Builder
    public TrainerRecommendationDto(long trainerId, long userId, String address, String name,
                                    String profileUrl, double rating, long totalReviews) {
        this.trainerId = trainerId;
        this.userId = userId;
        this.address = address;
        this.name = name;
        this.profileUrl = profileUrl;
        this.rating = rating;
        this.totalReviews = totalReviews;
    }

    public void updateTrainerInterests (List<Category> interests) {
        this.interests = interests;
    }


}
