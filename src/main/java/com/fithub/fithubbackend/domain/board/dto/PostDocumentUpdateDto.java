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

    private MultipartFile image;

    private String awsS3Url;

}
