package com.fithub.fithubbackend.domain.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "댓글 생성 dto")
public class CommentCreateDto {

    @NotNull
    @Schema(description = "댓글 내용")
    private String content;

    @NotNull
    @Schema(description = "게시글 Id")
    private long postId;

    @Schema(description = "상위 댓글 Id")
    private Long parentCommentId;

}
