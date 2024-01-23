package com.fithub.fithubbackend.domain.board.api;

import com.fithub.fithubbackend.domain.board.application.PostService;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Void> createPost(@Valid PostCreateDto postCreateDto, @AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");

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

    @Operation(summary = "게시글 전체 조회", responses = {
            @ApiResponse(responseCode = "200", description = "게시글 전체 조회 성공"),
    })
    @GetMapping("/public")
    public ResponseEntity<Page<PostOutlineDto>> getAllPosts(@PageableDefault(size = 9, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(postService.getAllPosts(pageable));
    }

    @Operation(summary = "게시글 세부 조회", responses = {
            @ApiResponse(responseCode = "200", description = "게시글 세부 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 게시글", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @GetMapping("/public/{postId}")
    public ResponseEntity<PostDetailInfoDto> getPostDetail(@PathVariable("postId") long postId) {
        return ResponseEntity.ok(postService.getPostDetail(postId));
    }

    @Operation(summary = "게시글 전체 조회 시 좋아요, 북마크 여부 체크 (로그인한 회원 ver)", responses = {
            @ApiResponse(responseCode = "200", description = "게시글 전체 조회 성공"),
    }, parameters = {
            @Parameter(name = "postOutlineDtos", description = "게시글 전체 조회하여 받은 response body의 content")
    })
    @GetMapping("/like-and-bookmark-status")
    public ResponseEntity<List<LikesBookmarkStatusDto>> getAllPostsWithLikesAndBookmark(@RequestBody List<PostOutlineDto> postOutlineDtos,
                                                                                        @AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(postService.checkPostsLikeAndBookmarkStatus(postOutlineDtos, user));
    }

    @Operation(summary = "게시글 세부 조회 시 좋아요, 북마크 여부 체크 (로그인한 회원 ver)", responses = {
            @ApiResponse(responseCode = "200", description = "게시글 전체 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 게시글", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @GetMapping("/{postId}/like-and-bookmark-status")
    public ResponseEntity<LikesBookmarkStatusDto> getAllPostsWithLikesAndBookmark(@AuthUser User user, @PathVariable("postId") long postId) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(postService.checkPostLikeAndBookmarkStatus(user, postId));
    }


    @Operation(summary = "게시글 좋아요", responses = {
            @ApiResponse(responseCode = "200", description = "게시글 좋아요 성공"),
            @ApiResponse(responseCode = "409", description = "이미 좋아요한 게시글", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 게시글", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
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
            @ApiResponse(responseCode = "404", description = "존재하지 않는 게시글", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
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
            @ApiResponse(responseCode = "409", description = "이미 북마크한 게시글", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 게시글", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
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
            @ApiResponse(responseCode = "404", description = "존재하지 않는 게시글", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
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
