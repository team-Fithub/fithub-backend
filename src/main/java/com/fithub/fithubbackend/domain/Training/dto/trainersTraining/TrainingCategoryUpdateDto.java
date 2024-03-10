package com.fithub.fithubbackend.domain.Training.dto.trainersTraining;

import com.fithub.fithubbackend.global.common.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "트레이닝 카테고리 수정 요청 Dto")
public class TrainingCategoryUpdateDto {

    @Schema(description = "삭제된 카테고리가 있다면 true 로 주면 됨.")
    private boolean categoryDeleted;

    @Schema(description = "categoryDeleted = true 일 때만 주면 됨. (false 일 경우 빈 리스트로 전달) 수정되지 않는 카테고리 리스트. ex) PILATES, HEALTH, PT, CROSSFIT, YOGA")
    private List<Category> unModifiedCategoryList;

    @Schema(description = "새로 추가된 카테고리가 있다면 true 로 주면 됨.")
    private boolean categoryAdded;

    @Schema(description = "categoryAdded = true 일 때만 주면 됨. (false 일 경우 빈 리스트로 전달) 새로 추가한 카테고리. ex) PILATES, HEALTH, PT, CROSSFIT, YOGA")
    private List<Category> newCategoryList;
}
