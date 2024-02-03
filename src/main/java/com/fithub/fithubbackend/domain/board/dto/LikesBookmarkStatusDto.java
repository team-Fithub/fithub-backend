package com.fithub.fithubbackend.domain.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LikesBookmarkStatusDto {

    @Schema(description = "게시글 id")
    private long postId;

    @Schema(description = "로그인한 사용자의 게시글 좋아요 여부 확인")
    private boolean likesStatus;

    @Schema(description = "로그인한 사용자의 게시글 북마크 여부 확인")
    private boolean bookmarkStatus;

    @Builder
    public LikesBookmarkStatusDto(long postId) {
        this.postId = postId;
        this.likesStatus = false;
        this.bookmarkStatus = false;
    }

    public void updateLikesStatus(boolean likesStatus) {
        this.likesStatus = likesStatus;
    }

    public void updateBookmarkStatus(boolean bookmarkStatus) {
        this.bookmarkStatus = bookmarkStatus;
    }
}
