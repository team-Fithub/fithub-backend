package com.fithub.fithubbackend.domain.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Schema(description = "게시글 이미지 수정 dto")
public class PostDocumentUpdateDto {

    private MultipartFile image;

    private String awsS3Url;

}
