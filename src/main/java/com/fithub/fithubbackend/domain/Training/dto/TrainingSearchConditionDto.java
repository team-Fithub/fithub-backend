package com.fithub.fithubbackend.domain.Training.dto;

import com.fithub.fithubbackend.global.common.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "트레이닝 검색 조건")
public class TrainingSearchConditionDto {

    @Schema(description = "검색 키워드, 해당 검색 키워드가 포함되는 제목으로 검색")
    private String keyword;

    @Schema(description = "최저가")
    private int lowestPrice;

    @Schema(description = "최고가")
    private int highestPrice;

    private LocalDate startDate;
    private LocalDate endDate;

    @Schema(description = "카테고리, 하나만 가능")
    private Category category;
}
