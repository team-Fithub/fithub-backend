package com.fithub.fithubbackend.domain.board.api;

import com.fithub.fithubbackend.domain.board.application.PostService;
import com.fithub.fithubbackend.domain.board.dto.PostCreateDto;
import com.fithub.fithubbackend.domain.board.dto.PostUpdateDto;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.global.domain.AuthUser;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import com.fithub.fithubbackend.global.exception.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @Operation(summary = "게시글 생성", responses = {
            @ApiResponse(responseCode = "200", description = "게시글 생성 완료"),
            @ApiResponse(responseCode = "500", description = "이미지 업로드 실패", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "최소 1개에서 최대 10개까지 이미지 업로드 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "이미지가 아닌 파일 업로드 또는 이미지 확장자 검사 실패", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createPost(@Valid PostCreateDto postCreateDto, BindingResult bindingResult, @AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        if(bindingResult.hasErrors())
            throw new CustomException(ErrorCode.NOT_FOUND, bindingResult.getFieldError().getDefaultMessage());

        postService.createPost(postCreateDto, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "게시글 수정", responses = {
            @ApiResponse(responseCode = "200", description = "게시글 수정 완료"),
            @ApiResponse(responseCode = "500", description = "이미지 업로드 실패", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 회원은 게시글 작성자가 아님", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "이미지가 아닌 파일 업로드 또는 이미지 확장자 검사 실패", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updatePost(@Valid PostUpdateDto postUpdateDto, @AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        postService.updatePost(postUpdateDto, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "게시글 삭제", responses = {
            @ApiResponse(responseCode = "200", description = "게시글 삭제 완료"),
            @ApiResponse(responseCode = "404", description = "해당 회원은 게시글 작성자가 아님", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "댓글이 있어 게시글 삭제 불가", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    }, parameters = {
            @Parameter(name = "postId", description = "삭제할 게시글 id")
    })
    @DeleteMapping
    public ResponseEntity<Void> deletePost(@RequestParam(value = "postId") long postId, @AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        postService.deletePost(postId, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "게시글 좋아요", responses = {
            @ApiResponse(responseCode = "200", description = "게시글 좋아요 성공"),
            @ApiResponse(responseCode = "409", description = "이미 좋아요한 게시글"),
    }, parameters = {
            @Parameter(name = "postId", description = "좋아요한 게시글 id")
    })
    @PostMapping("/likes")
    public ResponseEntity<Void> likesPost(@RequestParam(value = "postId") long postId, @AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        postService.likesPost(postId, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "게시글 좋아요 취소", responses = {
            @ApiResponse(responseCode = "200", description = "게시글 좋아요 취소 성공"),
    }, parameters = {
            @Parameter(name = "postId", description = "좋아요 취소할 게시글 id")
    })
    @DeleteMapping("/likes")
    public ResponseEntity<Void> notLikesPost(@RequestParam(value = "postId") long postId, @AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        postService.notLikesPost(postId, user);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "게시글 북마크", responses = {
            @ApiResponse(responseCode = "200", description = "게시글 북마크 성공"),
            @ApiResponse(responseCode = "409", description = "이미 북마크한 게시글"),
    }, parameters = {
            @Parameter(name = "postId", description = "북마크한 게시글 id")
    })
    @PostMapping("/bookmark")
    public ResponseEntity<Void> createBookMark(@RequestParam(value = "postId") long postId, @AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        postService.createBookmark(postId, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "게시글 북마크 삭제", responses = {
            @ApiResponse(responseCode = "200", description = "게시글 북마크 삭제 성공"),
    }, parameters = {
            @Parameter(name = "postId", description = "북마크 삭제할 게시글 id")
    })
    @DeleteMapping("/bookmark")
    public ResponseEntity<Void> deleteBookMark(@RequestParam(value = "postId") long postId, @AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        postService.deleteBookmark(postId, user);
        return ResponseEntity.ok().build();
    }

}
