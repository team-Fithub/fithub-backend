package com.fithub.fithubbackend.domain.trainer.dto;


import com.fithub.fithubbackend.domain.trainer.domain.Trainer;
import com.fithub.fithubbackend.domain.user.domain.UserInterest;
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
    public TrainerRecommendationDto(long trainerId, String address, String name,
                                    String profileUrl, List<Category> interests) {
        this.trainerId = trainerId;
        this.address = address;
        this.name = name;
        this.profileUrl = profileUrl;
        this.interests = interests;
    }

    public static TrainerRecommendationDto toDto(Trainer trainer) {
        return TrainerRecommendationDto.builder()
                .trainerId(trainer.getId())
                .address(trainer.getAddress())
                .name(trainer.getName())
                .profileUrl(trainer.getProfileUrl())
                .interests(trainer.getUser().getInterests().stream().map(UserInterest::getInterest).toList())
                .build();
    }

    public void updateRating(double rating) {
        this.rating = rating;
    }

    public void updateTotalReviews(long totalReviews) {
        this.totalReviews = totalReviews;
    }


}
