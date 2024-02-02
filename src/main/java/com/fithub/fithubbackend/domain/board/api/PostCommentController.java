package com.fithub.fithubbackend.domain.board.api;

import com.fithub.fithubbackend.domain.board.application.CommentService;
import com.fithub.fithubbackend.domain.board.dto.comment.CommentInfoDto;
import com.fithub.fithubbackend.domain.board.dto.comment.ParentCommentInfoDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

@Tag(name = "post's comment (게시글의 댓글, 답글)", description = "인증이 필요없는 댓글, 댓글의 답글 조회 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/comments")
public class PostCommentController {

    private final CommentService commentService;

    @Operation(summary = "최상위 댓글 조회. page 사용. (size = 10, sort = \"id\", desc 적용). 페이지 이동 시 page 값만 보내주면 됨. ex) \"page\" : 0 인 경우 1 페이지", responses = {
            @ApiResponse(responseCode = "200", description = "최상위 댓글 조회 완료"),
    }, parameters = {
            @Parameter(name="postId", description = "게시글 id")
    })
    @GetMapping("/{postId}")
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
    @GetMapping("/{postId}/{commentId}")
    public ResponseEntity<List<CommentInfoDto>> getDetailComments(@PathVariable(value = "postId") long postId, @PathVariable(value = "commentId") long commentId) {
        return ResponseEntity.ok().body(commentService.getDetailComments(commentId));
    }


}
