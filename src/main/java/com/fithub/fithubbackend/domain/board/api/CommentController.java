package com.fithub.fithubbackend.domain.board.api;

import com.fithub.fithubbackend.domain.board.application.CommentService;
import com.fithub.fithubbackend.domain.board.dto.CommentCreateDto;
import com.fithub.fithubbackend.domain.board.dto.CommentUpdateDto;
import com.fithub.fithubbackend.global.exception.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 등록", responses = {
            @ApiResponse(responseCode = "200", description = "댓글 등록 완료"),
    })
    @PostMapping
    public ResponseEntity<Void> createComment(@Valid CommentCreateDto commentCreateDto, @AuthenticationPrincipal UserDetails userDetails) {
        commentService.createComment(commentCreateDto, userDetails);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "댓글 수정", responses = {
            @ApiResponse(responseCode = "200", description = "댓글 수정 완료"),
            @ApiResponse(responseCode = "404", description = "댓글 작성자가 아니므로 댓글 수정 불가", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @PutMapping
    public ResponseEntity<Void> updateComment(@Valid CommentUpdateDto commentUpdateDto, @AuthenticationPrincipal UserDetails userDetails) {
        commentService.updateComment(commentUpdateDto, userDetails);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "댓글 삭제", responses = {
            @ApiResponse(responseCode = "200", description = "댓글 삭제 완료"),
            @ApiResponse(responseCode = "404", description = "댓글 작성자가 아니므로 댓글 삭제 불가", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteComment(@RequestParam(value = "commentId") long commentId, @AuthenticationPrincipal UserDetails userDetails) {
        commentService.deleteComment(commentId, userDetails);
        return ResponseEntity.ok().build();
    }

}
