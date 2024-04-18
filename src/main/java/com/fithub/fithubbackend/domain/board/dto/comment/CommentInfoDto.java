package com.fithub.fithubbackend.domain.board.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fithub.fithubbackend.domain.board.comment.domain.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "최상위 댓글의 답글 정보 dto")
public class CommentInfoDto {

    @Schema(description = "답글 id")
    private Long commentId;

    @Schema(description = "답글 작성자의 id")
    private Long writerId;

    @Schema(description = "답글 작성자의 닉네임")
    private String writerNickName;

    @Schema(description = "댓글 작성자의 이메일")
    private String writerEmail;

    @Schema(description = "답글 작성자의 프로필 url")
    private String writerProfileUrl;

    @Schema(description = "답글 내용")
    private String content;

    @Schema(description = "부모 댓글 작성자의 닉네임 (멘션 언급 시 사용 ex @fithub )")
    private String mentionedUserNickname;

    @Schema(description = "답글 삭제 여부")
    private boolean deleted;

    @Schema(description = "답글 생성일")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    @Schema(description = "답글 수정일")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDate;

    @Schema(description = "자식 답글 리스트")
    private List<CommentInfoDto> childComments = new ArrayList<>();

    @Builder
    public CommentInfoDto(Long commentId, Long writerId, String writerNickName, String writerEmail,
                          String content, String mentionedUserNickname,
                          String writerProfileUrl, boolean deleted, LocalDateTime createdDate,
                          LocalDateTime modifiedDate) {
        this.commentId = commentId;
        this.writerId = writerId;
        this.writerNickName = writerNickName;
        this.writerEmail = writerEmail;
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
                .writerId(comment.getUser().getId())
                .writerNickName(comment.getUser().getNickname())
                .writerEmail(comment.getUser().getEmail())
                .content(comment.getContent())
                .writerProfileUrl(comment.getUser().getProfileImg().getUrl())
                .createdDate(comment.getCreatedDate())
                .modifiedDate(comment.getModifiedDate())
                .mentionedUserNickname(comment.getParent().getUser().getNickname())
                .deleted(comment.getDeleted() == null ? false : comment.getDeleted()).build();
    }


}
