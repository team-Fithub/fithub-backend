package com.fithub.fithubbackend.domain.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PageableDto {

    @Schema(defaultValue = "0", description = "페이지 번호", required = true)
    private int page = 0;

    @Schema(defaultValue = "9", description = "페이지 크기")
    private int size = 9;

    @Schema(description = "정렬 방식", example = "[\"id\"]")
    private List<String> sort;


}
