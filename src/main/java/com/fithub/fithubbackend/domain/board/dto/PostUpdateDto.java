package com.fithub.fithubbackend.domain.board.dto;

import com.fithub.fithubbackend.domain.board.post.domain.PostDocument;
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

    @NotNull
    @Schema(description = "게시글 내용")
    private String content;

    @Schema(description = "게시글 작성자")
    private String userId;

    @Schema(description = "게시글 헤시태그")
    private String hashTags;

    @Schema(description = "게시글 이미지")
    private List<MultipartFile> images;

}
