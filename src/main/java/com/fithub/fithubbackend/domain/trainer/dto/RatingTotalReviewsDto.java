package com.fithub.fithubbackend.domain.trainer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RatingTotalReviewsDto {

    @Schema(description = "트레이너 평점")
    private double rating;

    @Schema(description = "트레이너 후기 수")
    private long totalReviews;

    public RatingTotalReviewsDto(double rating, long totalReviews) {
        this.rating = rating;
        this.totalReviews = totalReviews;
    }

}
