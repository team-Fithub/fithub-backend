package com.fithub.fithubbackend.domain.board.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LikesBookmarkStatusDto {

    private long postId;

    private boolean likesStatus;

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
