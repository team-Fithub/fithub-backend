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

    @Schema(description = "댓글 id")
    private Long commentId;

    @Schema(description = "댓글 작성자의 닉네임")
    private String writerNickName;

    @Schema(description = "댓글 내용")
    private String content;

    @Schema(description = "댓글 작성자의 프로필 url")
    private String writerProfileUrl;

    @Schema(description = "댓글 생성일")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    @Schema(description = "댓글 수정일")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDate;

    @Schema(description = "자식 답글 존재 여부")
    private Boolean hasChildComment;

    @Builder
    public ParentCommentInfoDto(Long commentId, String writerNickName, String content,
                                String writerProfileUrl, LocalDateTime createdDate,
                                LocalDateTime modifiedDate, Boolean hasChildComment) {
        this.commentId = commentId;
        this.writerNickName = writerNickName;
        this.content = content;
        this.writerProfileUrl = writerProfileUrl;
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
                .hasChildComment(comment.getChildren().isEmpty() ? false : true)
                .build();
    }
}
