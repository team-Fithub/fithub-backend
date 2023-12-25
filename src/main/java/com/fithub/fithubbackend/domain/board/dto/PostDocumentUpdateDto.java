package com.fithub.fithubbackend.domain.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Schema(description = "게시글 이미지 수정 dto")
public class PostDocumentUpdateDto {

    @NotNull
    private MultipartFile image;

    @Schema(description = "이미지 삭제 여부")
    private boolean deleted;

    @Schema(description = "이미지 추가 여부")
    private boolean added;
}
