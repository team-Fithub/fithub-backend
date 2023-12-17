package com.fithub.fithubbackend.domain.board.api;

import com.fithub.fithubbackend.domain.board.application.PostService;
import com.fithub.fithubbackend.domain.board.dto.PostCreateDto;
import com.fithub.fithubbackend.domain.board.dto.PostUpdateDto;
import com.fithub.fithubbackend.global.exception.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @Operation(summary = "게시글 생성", responses = {
            @ApiResponse(responseCode = "200", description = "게시글 생성 완료"),
            @ApiResponse(responseCode = "400", description = "이미지 업로드 실패", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createPost(@Valid PostCreateDto postCreateDto, @AuthenticationPrincipal UserDetails userDetails) {
        postService.createPost(postCreateDto, userDetails);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "게시글 수정", responses = {
            @ApiResponse(responseCode = "200", description = "게시글 수정 완료"),
            @ApiResponse(responseCode = "400", description = "이미지 업로드 실패", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updatePost(@Valid PostUpdateDto postUpdateDto, @AuthenticationPrincipal UserDetails userDetails) {
        postService.updatePost(postUpdateDto, userDetails);
        return ResponseEntity.ok().build();
    }
}
