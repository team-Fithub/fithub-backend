package com.fithub.fithubbackend.domain.board.api;

import com.fithub.fithubbackend.domain.board.application.CommentService;
import com.fithub.fithubbackend.domain.board.application.PostLikesService;
import com.fithub.fithubbackend.domain.board.application.PostService;
import com.fithub.fithubbackend.domain.board.dto.*;
import com.fithub.fithubbackend.domain.board.dto.comment.CommentInfoDto;
import com.fithub.fithubbackend.domain.board.dto.comment.ParentCommentInfoDto;
import com.fithub.fithubbackend.domain.board.dto.likes.LikedUsersInfoDto;
import com.fithub.fithubbackend.domain.board.dto.likes.LikesInfoDto;
import com.fithub.fithubbackend.global.exception.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "post (게시글)", description = "인증이 필요없는 게시글 조회 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final PostLikesService postLikesService;

    @Operation(summary = "게시글 전체 조회, page 사용 (size = 9, sort = \"id\", desc 적용). 페이지 이동 시 page 값만 보내주면 됨. ex) \"page\" : 0 인 경우 1 페이지", responses = {
            @ApiResponse(responseCode = "200", description = "게시글 전체 조회 성공"),
    })
    @GetMapping
    public ResponseEntity<Page<PostInfoDto>> getAllPosts(@PageableDefault(size = 9, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(postService.getAllPosts(pageable));
    }

    @Operation(summary = "게시글 세부 조회", responses = {
            @ApiResponse(responseCode = "200", description = "게시글 세부 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 게시글", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    }, parameters = {
            @Parameter(name="postId", description = "게시글 id")
    })
    @GetMapping("/{postId}")
    public ResponseEntity<PostInfoDto> getPostDetail(@PathVariable("postId") long postId) {
        return ResponseEntity.ok(postService.getPostDetail(postId));
    }

    @Operation(summary = "세부 게시글의 좋아요 정보 요약 조회 (개수와 최대 4개의 회원 정보)", responses = {
            @ApiResponse(responseCode = "200", description = "세부 게시글 좋아요 정보 요약 조회 성공"),
    }, parameters = {
            @Parameter(name="postId", description = "게시글 id")
    })
    @GetMapping("/{postId}/likes")
    public ResponseEntity<LikedUsersInfoDto> getLikedUsersForPostDetail(@PathVariable(value = "postId") Long postId) {
        return ResponseEntity.ok(postService.getLikedUsersForPostDetail(postId));
    }

    @Operation(summary = "세부 게시글의 전체 좋아요 조회. 페이징 사용", responses = {
            @ApiResponse(responseCode = "200", description = "세부 게시글 좋아요 정보 조회 성공"),
    }, parameters = {
            @Parameter(name="postId", description = "게시글 id"),
            @Parameter(name="pageable", description = "page (size = 20, sort = \"id\", desc 적용). 페이지 이동 시 page 값만 보내주면 됨. ex) \"page\" : 0 인 경우 1 페이지")
    })
    @GetMapping("/{postId}/likes/all")
    public ResponseEntity<Page<LikesInfoDto>> getAllLikedUsersForPostDetail(@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
                                                                            @PathVariable(value = "postId") Long postId) {
        return ResponseEntity.ok(postLikesService.getAllLikedUsersForPostDetail(pageable, postId));
    }

    @Operation(summary = "전체 게시글의 좋아요 정보 요약 조회 (개수와 최대 4개의 회원 정보)", responses = {
            @ApiResponse(responseCode = "200", description = "전체 게시글 좋아요 정보 요약 조회 성공"),
    }, parameters = {
            @Parameter(name="postRequestDtos", description = "전체 게시글 조회에서 받은 response body에서 postId을 추출하여 json 형식의 Request body로 전달")
    })
    @PostMapping("/likes")
    public ResponseEntity<List<LikedUsersInfoDto>> getLikedUsersForPostDetail(@RequestBody List<PostRequestDto> postRequestDtos) {
        return ResponseEntity.ok(postService.getLikedUsersForPosts(postRequestDtos));
    }

    @Operation(summary = "최상위 댓글 조회. page 사용. (size = 10, sort = \"id\", desc 적용). 페이지 이동 시 page 값만 보내주면 됨. ex) \"page\" : 0 인 경우 1 페이지", responses = {
            @ApiResponse(responseCode = "200", description = "최상위 댓글 조회 완료"),
    }, parameters = {
            @Parameter(name="postId", description = "게시글 id")
    })
    @GetMapping("/{postId}/comments")
    public ResponseEntity<Page<ParentCommentInfoDto>> getComments(@PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                                                  @PathVariable(value = "postId") long postId) {
        return ResponseEntity.ok().body(commentService.getCommentsWithPage(pageable, postId));
    }

    @Operation(summary = "대댓글 조회", responses = {
            @ApiResponse(responseCode = "200", description = "대댓글 조회 완료"),
    }, parameters = {
            @Parameter(name="postId", description = "게시글 id"),
            @Parameter(name="commentId", description = "최상위 댓글 id")
    })
    @GetMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<List<CommentInfoDto>> getDetailComments(@PathVariable(value = "postId") long postId, @PathVariable(value = "commentId") long commentId) {
        return ResponseEntity.ok().body(commentService.getDetailComments(commentId));
    }

}
