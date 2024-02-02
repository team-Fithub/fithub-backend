package com.fithub.fithubbackend.domain.board.api;

import com.fithub.fithubbackend.domain.board.application.UserPostService;
import com.fithub.fithubbackend.domain.board.dto.*;
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
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "user's post (회원의 게시글)", description = "회원이 사용하는 게시글 관련 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/posts")
public class UserPostController {

    private final UserPostService userPostService;

    @Operation(summary = "게시글 생성", responses = {
            @ApiResponse(responseCode = "200", description = "게시글 생성 완료"),
            @ApiResponse(responseCode = "500", description = "이미지 업로드 실패", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "최소 1개에서 최대 10개까지 이미지 업로드 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "이미지가 아닌 파일 업로드 또는 이미지 확장자 검사 실패", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createPost(@Valid PostCreateDto postCreateDto, @AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");

        userPostService.createPost(postCreateDto, user);
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
        userPostService.updatePost(postUpdateDto, user);
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
        userPostService.deletePost(postId, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "게시글 전체 조회 시 좋아요, 북마크 여부 체크 (로그인한 회원 ver)", responses = {
            @ApiResponse(responseCode = "200", description = "게시글 전체 조회 성공"),
    }, parameters = {
            @Parameter(name="postRequestDtos", description = "전체 게시글 조회에서 받은 response body에서 postId을 추출하여 json 형식의 Request body로 전달")
    })
    @PostMapping("/status/like-bookmark")
    public ResponseEntity<List<LikesBookmarkStatusDto>> getAllPostsWithLikesAndBookmark(@RequestBody List<PostRequestDto> postRequestDtos,
                                                                                        @AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(userPostService.checkPostsLikeAndBookmarkStatus(postRequestDtos, user));
    }

    @Operation(summary = "게시글 세부 조회 시 좋아요, 북마크 여부 체크 (로그인한 회원 ver)", responses = {
            @ApiResponse(responseCode = "200", description = "게시글 전체 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 게시글", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @GetMapping("/{postId}/status/like-bookmark")
    public ResponseEntity<LikesBookmarkStatusDto> getAllPostsWithLikesAndBookmark(@AuthUser User user, @PathVariable("postId") long postId) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(userPostService.checkPostLikeAndBookmarkStatus(user, postId));
    }

}
