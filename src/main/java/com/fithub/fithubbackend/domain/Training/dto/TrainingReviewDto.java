package com.fithub.fithubbackend.domain.Training.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fithub.fithubbackend.domain.Training.domain.TrainingReview;
import com.fithub.fithubbackend.domain.user.dto.UserNicknameAndProfileDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Schema(description = "트레이닝 상세조회에서 후기 리스트 조회 시에 사용하는 dto - 후기 남긴 사용자 정보 포함")
public class TrainingReviewDto {

    private Long reviewId;

    private UserNicknameAndProfileDto userInfo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reserveDateTime;

    private String content;
    private int star;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime modifiedDate;

    public static TrainingReviewDto toDto(TrainingReview trainingReview) {
        return TrainingReviewDto.builder()
                .reviewId(trainingReview.getId())
                .userInfo(UserNicknameAndProfileDto.toDto(trainingReview.getUser()))
                .reserveDateTime(trainingReview.getReserveInfo().getReserveDateTime())
                .content(trainingReview.getContent())
                .star(trainingReview.getStar())
                .createdDate(trainingReview.getCreatedDate())
                .modifiedDate(trainingReview.getModifiedDate())
                .build();
    }
}
