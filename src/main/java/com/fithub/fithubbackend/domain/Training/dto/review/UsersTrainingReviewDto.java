package com.fithub.fithubbackend.domain.Training.dto.review;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fithub.fithubbackend.domain.Training.domain.TrainingReview;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UsersTrainingReviewDto {
    private Long reviewId;
    private Long trainingId;
    private String trainingTitle;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reserveDateTime;
    private String content;
    private int star;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime modifiedDate;

    public static UsersTrainingReviewDto toDto(TrainingReview review) {
        return UsersTrainingReviewDto.builder()
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
