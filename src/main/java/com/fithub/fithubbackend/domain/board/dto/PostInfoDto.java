package com.fithub.fithubbackend.domain.board.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fithub.fithubbackend.domain.board.comment.domain.Comment;
import com.fithub.fithubbackend.domain.board.post.domain.Bookmark;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Schema(description = "게시글 dto")
public class PostInfoDto {

    @Schema(description = "게시글 id")
    private Long postId;

    @Schema(description = "게시글 생성일")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime postCreatedDate;

    @Schema(description = "게시글 내용")
    private String postContent;

    @Schema(description = "게시글 작성자")
    private String postWriter;

    @Schema(description = "게시글 작성자 프로필 url")
    private String postWriterProfileUrl;

    @Schema(description = "게시글 해시태그")
    private List<String> postHashTags;

    @Schema(description = "게시글 조회수")
    private Integer postViews;

    @Schema(description = "게시글 좋아요 수")
    private Long postLikesCount;

    @Schema(description = "게시글 좋아요 리스트")
    private List<LikesInfoDto> postLikedUser;

    @JsonIgnore
    private List<Bookmark> postBookmarkedUser;

    @Schema(description = "게시글 첨부 이미지 url 리스트")
    private List<String> postDocumentUrls;

    @Schema(description = "게시글 댓글 수")
    private Integer postCommentsCount;

    @Schema(description = "게시글 댓글 리스트")
    private List<CommentInfoDto> postComments;

    @Schema(description = "게시글 좋아요 여부")
    private boolean isLiked;

    @Schema(description = "게시글 북마크 여부")
    private boolean isBookmark;

    public void setComment(List<CommentInfoDto> comments) {
        this.postComments = comments;
    }

    public void checkLikes(boolean isLiked) {
        this.isLiked = isLiked;
    }

    public void checkBookmark(boolean isBookmark) {
        this.isBookmark = isBookmark;
    }

    @Builder
    public PostInfoDto(Long postId, String postContent, String postWriter, String postWriterProfileUrl, List<String> postHashTags,
                       Integer postViews, Long postLikesCount, List<LikesInfoDto> postLikedUser, List<String> postDocumentUrls, Integer postCommentsCount,
                       List<Bookmark> postBookmarkedUser, LocalDateTime postCreatedDate) {
        this.postId = postId;
        this.postContent = postContent;
        this.postWriter = postWriter;
        this.postWriterProfileUrl = postWriterProfileUrl;
        this.postHashTags = postHashTags;
        this.postViews = postViews;
        this.postLikesCount = postLikesCount;
        this.postLikedUser = postLikedUser;
        this.postDocumentUrls = postDocumentUrls;
        this.postCommentsCount = postCommentsCount;
        this.postBookmarkedUser = postBookmarkedUser;
        this.postCreatedDate = postCreatedDate;
    }

    public static PostInfoDto fromEntity(Post post) {

        Integer commentsCount = 0;
        for (Comment comment: post.getComments()) 
            if (comment.getDeleted() == null)
                commentsCount++;
        
        return PostInfoDto.builder()
                .postId(post.getId())
                .postContent(post.getContent())
                .postWriter(post.getUser().getNickname())
                .postWriterProfileUrl(post.getUser().getProfileImg().getUrl())
                .postViews(post.getViews())
                .postLikesCount((long) post.getLikes().size())
                .postCommentsCount(commentsCount)
                .postLikedUser(post.getLikes().stream().map(LikesInfoDto::fromEntity).collect(Collectors.toList()))
                .postHashTags(post.getPostHashtags().stream().map(hashtag -> hashtag.getHashtag().getContent()).collect(Collectors.toList()))
                .postDocumentUrls(post.getPostDocuments().stream().map(postDocument -> postDocument.getUrl()).collect(Collectors.toList()))
                .postBookmarkedUser(post.getBookmarks())
                .postCreatedDate(post.getCreatedDate())
                .build();
    }
}
