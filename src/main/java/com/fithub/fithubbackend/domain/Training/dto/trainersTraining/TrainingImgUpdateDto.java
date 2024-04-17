package com.fithub.fithubbackend.domain.Training.dto.trainersTraining;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "트레이닝 내용 수정 - 이미지 수정에 사용")
public class TrainingImgUpdateDto {

    @Schema(description = "삭제된 이미지가 있다면 true로 주면 됨")
    private boolean imgDeleted;
    @Schema(description = "imgDeleted = true일 때만 주면 됨, 남아있는 이미지 url 리스트")
    private List<String> unModifiedImgList;

    @Schema(description = "새로 추가된 이미지가 있다면 true로")
    private boolean imgAdded;
    @Schema(description = "새로 추가한 이미지")
    private List<MultipartFile> newImgList;
}
