package com.fithub.fithubbackend.domain.board.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "댓글 수정 dto")
public class CommentUpdateDto {

    @NotNull
    @Schema(description = "댓글 내용")
    private String content;

    @NotNull
    @Schema(description = "댓글 Id")
    private long commentId;

}
