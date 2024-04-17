package com.fithub.fithubbackend.domain.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

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
    @Schema(description = "기존 게시글 이미지 삭제 여부")
    private boolean isImageDeleted;

    @NotNull
    @Schema(description = "게시글 이미지 추가 여부")
    private boolean isImageAdded;

    @Schema(description = "추가된 게시글 이미지 파일")
    private List<MultipartFile> newImages;

    @Schema(description = "삭제 되지 않는 게시글 이미지 aws s3의 url")
    private List<String> existingImages = new ArrayList<>();

}
