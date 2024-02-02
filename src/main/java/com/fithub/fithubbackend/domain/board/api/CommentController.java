package com.fithub.fithubbackend.domain.board.api;

import com.fithub.fithubbackend.domain.board.application.CommentService;
import com.fithub.fithubbackend.domain.board.dto.CommentCreateDto;
import com.fithub.fithubbackend.domain.board.dto.CommentInfoDto;
import com.fithub.fithubbackend.domain.board.dto.ParentCommentInfoDto;
import com.fithub.fithubbackend.domain.board.dto.CommentUpdateDto;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 등록", responses = {
            @ApiResponse(responseCode = "200", description = "댓글 등록 완료"),
    })
    @PostMapping
    public ResponseEntity<Void> createComment(@RequestBody @Valid CommentCreateDto commentCreateDto, @AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        commentService.createComment(commentCreateDto, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "댓글 수정", responses = {
            @ApiResponse(responseCode = "200", description = "댓글 수정 완료"),
            @ApiResponse(responseCode = "404", description = "댓글 작성자가 아니므로 댓글 수정 불가", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @PutMapping
    public ResponseEntity<Void> updateComment(@RequestBody @Valid CommentUpdateDto commentUpdateDto, @AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        commentService.updateComment(commentUpdateDto, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "댓글 삭제", responses = {
            @ApiResponse(responseCode = "200", description = "댓글 삭제 완료"),
            @ApiResponse(responseCode = "404", description = "댓글 작성자가 아니므로 댓글 삭제 불가", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteComment(@RequestParam(value = "commentId") long commentId, @AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        commentService.deleteComment(commentId, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "최상위 댓글 조회. page 사용. (size = 10, sort = \"id\", desc 적용). 페이지 이동 시 page 값만 보내주면 됨. ex) \"page\" : 0 인 경우 1 페이지", responses = {
            @ApiResponse(responseCode = "200", description = "최상위 댓글 조회 완료"),
    }, parameters = {
            @Parameter(name="postId", description = "게시글 id")
    })
    @GetMapping("/public/{postId}")
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
    @GetMapping("/public/{postId}/{commentId}")
    public ResponseEntity<List<CommentInfoDto>> getDetailComments(@PathVariable(value = "postId") long postId, @PathVariable(value = "commentId") long commentId) {
        return ResponseEntity.ok().body(commentService.getDetailComments(commentId));
    }


}
