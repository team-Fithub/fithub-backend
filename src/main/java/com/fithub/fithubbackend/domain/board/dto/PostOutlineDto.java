package com.fithub.fithubbackend.domain.board.dto;

import com.fithub.fithubbackend.domain.board.comment.domain.Comment;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "게시글 요약 정보 dto")
public class PostOutlineDto {

    @Schema(description = "게시글 정보")
    private PostInfoDto postInfo;

    @Schema(description = "게시글 좋아요 정보")
    private LikedUsersInfoDto postLikedInfo;

    @Schema(description = "게시글 댓글 수")
    private Integer postCommentsCount;

    @Builder
    public PostOutlineDto(PostInfoDto postInfo, LikedUsersInfoDto postLikedInfo,
                          Integer postCommentsCount) {
        this.postInfo = postInfo;
        this.postLikedInfo = postLikedInfo;
        this.postCommentsCount = postCommentsCount;
    }

    public static PostOutlineDto toDto(Post post) {

        Integer commentsCount = 0;
        for (Comment comment: post.getComments())
            if (comment.getDeleted() == null)
                commentsCount++;

        return PostOutlineDto.builder()
                .postInfo(PostInfoDto.toDto(post))
                .postLikedInfo(LikedUsersInfoDto.toDo(post))
                .postCommentsCount(commentsCount)
                .build();
    }
}
