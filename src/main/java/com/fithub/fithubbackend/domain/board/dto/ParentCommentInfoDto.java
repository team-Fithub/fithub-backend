package com.fithub.fithubbackend.domain.board.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fithub.fithubbackend.domain.board.comment.domain.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "최 상위 댓글 정보 dto")
public class ParentCommentInfoDto {

    private Long commentId;

    private String writerNickName;

    private String content;

    private String writerProfileUrl;

    private boolean deleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDate;

    private Boolean hasChildComment;

    @Builder
    public ParentCommentInfoDto(Long commentId, String writerNickName, String content,
                                String writerProfileUrl, boolean deleted, LocalDateTime createdDate,
                                LocalDateTime modifiedDate, Boolean hasChildComment) {
        this.commentId = commentId;
        this.writerNickName = writerNickName;
        this.content = content;
        this.writerProfileUrl = writerProfileUrl;
        this.deleted = deleted;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.hasChildComment = hasChildComment;
    }

    public static ParentCommentInfoDto toDto(Comment comment) {

        return ParentCommentInfoDto.builder()
                .commentId(comment.getId())
                .writerNickName(comment.getUser().getNickname())
                .content(comment.getContent())
                .writerProfileUrl(comment.getUser().getProfileImg().getUrl())
                .createdDate(comment.getCreatedDate())
                .modifiedDate(comment.getModifiedDate())
                .deleted(comment.getDeleted() == null ? false : comment.getDeleted())
                .hasChildComment(comment.getChildren().isEmpty() ? false : true)
                .build();
    }
}
