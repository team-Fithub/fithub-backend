package com.fithub.fithubbackend.domain.board.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fithub.fithubbackend.domain.board.comment.domain.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "답글 정보 dto")
public class CommentInfoDto {

    private Long commentId;

    private String writerNickName;

    private String writerProfileUrl;

    private String content;

    private String mentionedUserNickname;


    private boolean deleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDate;

    @Builder
    public CommentInfoDto(Long commentId, String writerNickName, String content, String mentionedUserNickname,
                          String writerProfileUrl, boolean deleted, LocalDateTime createdDate,
                          LocalDateTime modifiedDate) {
        this.commentId = commentId;
        this.writerNickName = writerNickName;
        this.content = content;
        this.writerProfileUrl = writerProfileUrl;
        this.deleted = deleted;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.mentionedUserNickname = mentionedUserNickname;
    }

    public static CommentInfoDto toDto(Comment comment) {

        return CommentInfoDto.builder()
                .commentId(comment.getId())
                .writerNickName(comment.getUser().getNickname())
                .content(comment.getContent())
                .writerProfileUrl(comment.getUser().getProfileImg().getUrl())
                .createdDate(comment.getCreatedDate())
                .modifiedDate(comment.getModifiedDate())
                .mentionedUserNickname(comment.getParent().getUser().getNickname())
                .deleted(comment.getDeleted() == null ? false : comment.getDeleted()).build();
    }


}
