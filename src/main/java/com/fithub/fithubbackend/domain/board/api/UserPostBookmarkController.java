package com.fithub.fithubbackend.domain.board.api;

import com.fithub.fithubbackend.domain.board.application.UserPostBookmarkService;
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
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "user's post bookmark (회원의 게시글 북마크)", description = "회원이 사용하는 게시글 북마크 관련 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/posts/bookmarks")
public class UserPostBookmarkController {

    private final UserPostBookmarkService userPostBookmarkService;

    @Operation(summary = "게시글 북마크", responses = {
            @ApiResponse(responseCode = "200", description = "게시글 북마크 성공"),
            @ApiResponse(responseCode = "409", description = "이미 북마크한 게시글", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 게시글", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    }, parameters = {
            @Parameter(name = "postId", description = "북마크한 게시글 id")
    })
    @PostMapping
    public ResponseEntity<Void> createBookMark(@RequestParam(value = "postId") long postId, @AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        userPostBookmarkService.addBookmark(user, postId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "게시글 북마크 삭제", responses = {
            @ApiResponse(responseCode = "200", description = "게시글 북마크 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 게시글", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    }, parameters = {
            @Parameter(name = "postId", description = "북마크 삭제할 게시글 id")
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteBookMark(@RequestParam(value = "postId") long postId, @AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        userPostBookmarkService.deleteBookmark(user, postId);
        return ResponseEntity.ok().build();
    }
}
