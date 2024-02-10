package com.fithub.fithubbackend.domain.Training.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fithub.fithubbackend.domain.Training.dto.review.TrainingReviewReqDto;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.global.common.BaseTimeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @NotNull
    @Schema(description = "비공개(잠금) 여부 (트레이너에 의해 / 관리자에 의해 잠금처리)")
    private boolean locked;

    @Builder
    private TrainingReview(User user, ReserveInfo reserveInfo, TrainingReviewReqDto trainingReviewReqDto) {
        this.user = user;
        this.reserveInfo = reserveInfo;
        this.training = reserveInfo.getTraining();
        this.content = trainingReviewReqDto.getContent();
        this.star = trainingReviewReqDto.getStar();
        this.locked = false;
    }

    public void updateReview(String content, int star) {
        this.content = content;
        this.star = star;
    }

    public void lock() {
        this.locked = true;
    }
}
