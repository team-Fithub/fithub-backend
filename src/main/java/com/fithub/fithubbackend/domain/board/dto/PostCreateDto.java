package com.fithub.fithubbackend.domain.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Schema(description = "게시글 생성 dto")
public class PostCreateDto {


    @Schema(description = "게시글 내용")
    private String content;

    @Schema(description = "게시글 해시태그")
    private String hashTags;

    @NotNull(message = "게시글 이미지는 최소 1개 이상 업로드")
    @Size(min = 1, max = 10, message = "게시글 이미지는 최소 1개에서 최대 10개까지 업로드 가능")
    @Schema(description = "게시글 이미지")
    private List<MultipartFile> images;


}
