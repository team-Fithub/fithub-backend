package com.fithub.fithubbackend.domain.Training.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fithub.fithubbackend.domain.Training.dto.review.TrainingReviewReqDto;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainingReview extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Training training;

    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"training"})
    private ReserveInfo reserveInfo;

    @NotNull
    private String content;

    @NotNull
    private int star;

    @Builder
    private TrainingReview(User user, ReserveInfo reserveInfo, TrainingReviewReqDto trainingReviewReqDto) {
        this.user = user;
        this.reserveInfo = reserveInfo;
        this.training = reserveInfo.getTraining();
        this.content = trainingReviewReqDto.getContent();
        this.star = trainingReviewReqDto.getStar();
    }

    public void updateReview(String content, int star) {
        this.content = content;
        this.star = star;
    }
}
