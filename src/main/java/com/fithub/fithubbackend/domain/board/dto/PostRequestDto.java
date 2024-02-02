package com.fithub.fithubbackend.domain.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "전체 게시글 조회 시 받은 response body의 게시글 id")
public class PostRequestDto {

    @Schema(description = "게시글 id")
    private Long postId;

}
