package com.fithub.fithubbackend.domain.board.dto;

import com.fithub.fithubbackend.domain.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "게시글 생성 dto")
public class PostCreateDto {

    @NotNull
    @Schema(description = "게시글 내용")
    private String content;

    @Schema(description = "게시글 해시태그")
    private String hashTags;


}
