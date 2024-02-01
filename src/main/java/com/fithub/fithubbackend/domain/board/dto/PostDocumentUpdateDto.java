package com.fithub.fithubbackend.domain.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "게시글 이미지 수정 dto")
public class PostDocumentUpdateDto {

    @Schema(description = "새로 추가된 이미지 파일")
    private MultipartFile image;

    @Schema(description = "수정되지 않는 이미지 url")
    private String awsS3Url;

}
