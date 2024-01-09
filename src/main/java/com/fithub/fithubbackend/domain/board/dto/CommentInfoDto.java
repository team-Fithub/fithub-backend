package com.fithub.fithubbackend.domain.board.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fithub.fithubbackend.domain.board.comment.domain.Comment;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.*;

@Data
public class CommentInfoDto {

    private Long commentId;

    private String writerNickName;

    private String content;

    private Long parentCommentId;

    private String profileUrl;

    private String profileInputName;

    private boolean deleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;


    private List<CommentInfoDto> childComment = new ArrayList<>();


    public static CommentInfoDto fromEntity(Comment comment) {
        CommentInfoDto commentInfoDto = new CommentInfoDto();

        commentInfoDto.setCommentId(comment.getId());
        commentInfoDto.setDeleted(comment.getDeleted() == null ? false : comment.getDeleted());
        commentInfoDto.setWriterNickName(comment.getUser().getNickname());
        commentInfoDto.setProfileUrl(comment.getUser().getProfileImg().getUrl());
        commentInfoDto.setProfileInputName(comment.getUser().getProfileImg().getInputName());
        commentInfoDto.setContent(comment.getContent());
        commentInfoDto.setParentCommentId(comment.getParent() == null ? null : comment.getParent().getId());
        commentInfoDto.setCreatedDate(comment.getCreatedDate());

        return commentInfoDto;
    }


}
