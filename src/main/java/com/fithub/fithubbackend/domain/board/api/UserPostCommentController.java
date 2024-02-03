package com.fithub.fithubbackend.domain.board.api;

import com.fithub.fithubbackend.domain.board.application.UserCommentService;
import com.fithub.fithubbackend.domain.board.dto.PostInfoDto;
import com.fithub.fithubbackend.domain.board.dto.comment.CommentCreateDto;
import com.fithub.fithubbackend.domain.board.dto.comment.CommentUpdateDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "user's post comment (회원의 게시글 댓글)", description = "회원이 사용하는 댓글 관련 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/posts/comments")
public class UserPostCommentController {

    private final UserCommentService userCommentService;

    @Operation(summary = "댓글 등록", responses = {
            @ApiResponse(responseCode = "200", description = "댓글 등록 완료"),
    })
    @PostMapping
    public ResponseEntity<Void> createComment(@RequestBody @Valid CommentCreateDto commentCreateDto, @AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        userCommentService.createComment(commentCreateDto, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "댓글 수정", responses = {
            @ApiResponse(responseCode = "200", description = "댓글 수정 완료"),
            @ApiResponse(responseCode = "404", description = "댓글 작성자가 아니므로 댓글 수정 불가", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @PutMapping
    public ResponseEntity<Void> updateComment(@RequestBody @Valid CommentUpdateDto commentUpdateDto, @AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        userCommentService.updateComment(commentUpdateDto, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "댓글 삭제", responses = {
            @ApiResponse(responseCode = "200", description = "댓글 삭제 완료"),
            @ApiResponse(responseCode = "404", description = "댓글 작성자가 아니므로 댓글 삭제 불가", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteComment(@RequestParam(value = "commentId") long commentId, @AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        userCommentService.deleteComment(commentId, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원이 댓글 남긴 게시글 조회. page 사용", responses = {
            @ApiResponse(responseCode = "200", description = "게시글 조회 성공")
    }, parameters = {
            @Parameter(name="pageable", description = "page (size = 9, sort = \"id\", desc 적용). 페이지 이동 시 page 값만 보내주면 됨. ex) \"page\" : 0 인 경우 1 페이지")
    })
    @GetMapping
    public ResponseEntity<Page<PostInfoDto>> getPostsByUserAndComments(@PageableDefault(size = 9, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                                            @AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(userCommentService.getPostsByUserAndComments(pageable, user));
    }
}
