package com.fithub.fithubbackend.domain.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "게시글 dto")
public class PostInfoDto {

    @NotNull
    @Schema(description = "게시글 id")
    private Long id;

    @NotNull
    @Schema(description = "게시글 내용")
    private String content;

    @Schema(description = "게시글 작성자")
    private String userId;

    @Schema(description = "게시글 헤시태그")
    private String hashTags;

}
