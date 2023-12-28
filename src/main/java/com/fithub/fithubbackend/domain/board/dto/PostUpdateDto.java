package com.fithub.fithubbackend.domain.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.*;

@Data
@Schema(description = "게시글 수정 dto")
public class PostUpdateDto {

    @NotNull
    @Schema(description = "게시글 id")
    private Long id;

    @Schema(description = "게시글 내용")
    private String content;

    @Schema(description = "게시글 해시태그")
    private String hashTags;

    @NotNull
    @Schema(description = "게시글 이미지 변경 여부")
    private boolean isImageChanged;

    @NotNull
    @Schema(description = "수정된 게시글 이미지")
    private List<PostDocumentUpdateDto> editedImages;


}
