package com.fithub.fithubbackend.domain.board.post.domain;

import com.fithub.fithubbackend.domain.board.comment.domain.Comment;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @NotNull
    private Integer views;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;


    @BatchSize(size = 100)
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Bookmark> bookmarks = new ArrayList<>();

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Likes> likes = new ArrayList<>();

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<PostHashtag> postHashtags = new ArrayList<>();

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<PostDocument> postDocuments = new ArrayList<>();

    public void setUser(User user) {
        this.user = user;
    }

    @Builder
    public Post(String content, User user){
        this.content = content;
        this.user = user;
        this.views = 0;
    }

    public void updatePost(String content) {
        this.content = content;
    }

    public void addPostHashtag(PostHashtag postHashtag) {
        this.postHashtags.add(postHashtag);

        if (postHashtag.getPost() != this)
            postHashtag.setPost(this);
    }

    public void addPostDocument(PostDocument postDocument) {
        this.postDocuments.add(postDocument);

        if (postDocument.getPost() != this)
            postDocument.setPost(this);
    }

    public void addBookmark(Bookmark bookmark) {
        this.bookmarks.add(bookmark);

        if (bookmark.getPost() != this)
            bookmark.setPost(this);
    }

    public void addLikes(Likes likes) {
        this.likes.add(likes);

        if (likes.getPost() != this)
            likes.setPost(this);
    }


}
