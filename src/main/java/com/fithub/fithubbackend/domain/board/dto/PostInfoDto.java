package com.fithub.fithubbackend.domain.board.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fithub.fithubbackend.domain.board.comment.domain.Comment;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "게시글 정보 dto")
public class PostInfoDto {

    @Schema(description = "게시글 id")
    private Long postId;

    @Schema(description = "게시글 작성자 정보")
    private PostWriterInfoDto writerInfo;

    @Schema(description = "게시글 생성일")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    @Schema(description = "게시글 수정일")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDate;

    @Schema(description = "게시글 내용")
    private String content;

    @Schema(description = "게시글 해시태그")
    private List<String> hashTags;

    @Schema(description = "게시글 조회수")
    private Integer views;

    @Schema(description = "게시글 첨부 이미지 url 리스트")
    private List<String> documentUrls;

    @Schema(description = "게시글 댓글 수")
    private Integer postCommentsCount;

    @Builder
    public PostInfoDto(Long postId, String content, Integer views, PostWriterInfoDto writerInfo,
                       List<String> documentUrls, List<String> hashTags, LocalDateTime createdDate,
                       LocalDateTime modifiedDate, Integer postCommentsCount) {
        this.postId = postId;
        this.writerInfo = writerInfo;
        this.content = content;
        this.hashTags = hashTags;
        this.views = views;
        this.documentUrls = documentUrls;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.postCommentsCount = postCommentsCount;
    }

    public static PostInfoDto toDto(Post post) {

        Integer commentsCount = 0;
        for (Comment comment: post.getComments())
            if (comment.getDeleted() == null && comment.getParent() == null)
                commentsCount++;

        return PostInfoDto.builder()
                .postId(post.getId())
                .writerInfo(PostWriterInfoDto.toDto(post.getUser()))
                .content(post.getContent())
                .views(post.getViews())
                .hashTags(post.getPostHashtags().stream().map(hashtag -> hashtag.getHashtag().getContent()).collect(Collectors.toList()))
                .documentUrls(post.getPostDocuments().stream().map(postDocument -> postDocument.getUrl()).collect(Collectors.toList()))
                .createdDate(post.getCreatedDate())
                .modifiedDate(post.getModifiedDate())
                .postCommentsCount(commentsCount)
                .build();
    }
}
