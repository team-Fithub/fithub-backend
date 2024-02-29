package com.fithub.fithubbackend.domain.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "게시글 검색 필터 dto")
public class PostSearchFilterDto {

    @Schema(description = "검색 키워드", example = "검색 키워드")
    private String keyword;

    @Schema(description = "검색 범위 (내용, 작성자, 해시태그)", example = "content")
    private String scope = "content";

}
