package com.fithub.fithubbackend.domain.Training.dto;

import com.fithub.fithubbackend.domain.Training.domain.TrainingReview;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TrainingReviewInfoDto {
    private Long reviewId;
    private Long trainingId;
    private String trainingTitle;
    private LocalDateTime reserveDateTime;
    private String content;
    private int star;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public static TrainingReviewInfoDto toDto(TrainingReview review) {
        return TrainingReviewInfoDto.builder()
                .reviewId(review.getId())
                .trainingId(review.getTraining().getId())
                .trainingTitle(review.getTraining().getTitle())
                .reserveDateTime(review.getReserveInfo().getReserveDateTime())
                .content(review.getContent())
                .star(review.getStar())
                .createdDate(review.getCreatedDate())
                .modifiedDate(review.getModifiedDate())
                .build();
    }
}
