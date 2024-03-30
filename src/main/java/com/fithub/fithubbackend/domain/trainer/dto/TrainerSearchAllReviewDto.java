package com.fithub.fithubbackend.domain.trainer.dto;

import com.fithub.fithubbackend.domain.Training.dto.review.UsersTrainingReviewDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainerSearchAllReviewDto {
    private Long trainerId;

    private Double average;
    private int reviewNum;

    private List<UsersTrainingReviewDto> usersReviewList;

    @Builder
    public TrainerSearchAllReviewDto(Long trainerId, Double average, int reviewNum, List<UsersTrainingReviewDto> list) {
        this.trainerId = trainerId;
        this.average = average;
        this.reviewNum = reviewNum;
        this.usersReviewList = list;
    }
}
