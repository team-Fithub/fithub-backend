package com.fithub.fithubbackend.domain.Training.dto.trainersTraining;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@Schema(description = "트레이닝 내용 수정 요청 Dto")
public class TrainingContentUpdateDto {
    @NotNull
    private String title;

    @NotNull
    private String content;

    @NotNull
    private int price;

    @Schema(description = "삭제된 이미지가 있다면 true로 주면 됨")
    private boolean imgDeleted;
    @Schema(description = "imgDeleted = true일 때만 주면 됨, 남아있는 이미지 url 리스트")
    private List<String> unModifiedImgList;

    @Schema(description = "새로 추가된 이미지가 있다면 true로")
    private boolean imgAdded;
    @Schema(description = "새로 추가한 이미지")
    private List<MultipartFile> newImgList;

    @Schema(description = "트레이닝 카테고리를 수정했다면 값을 넣어주면 됨. 수정 없으면 안 줘야 됨")
    private TrainingCategoryUpdateDto trainingCategoryUpdateDto;
}
