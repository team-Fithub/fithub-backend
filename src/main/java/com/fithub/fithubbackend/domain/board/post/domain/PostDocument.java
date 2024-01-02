package com.fithub.fithubbackend.domain.board.post.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String url;

    @NotNull
    private String inputName;

    @NotNull
    @Comment("파일 경로")
    private String path;

    @NotNull
    @Comment("파일 사이즈")
    private long size;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    public PostDocument(String url, String inputName, String path, Post post, long size) {
        this.url = url;
        this.inputName = inputName;
        this.path = path;
        this.post = post;
        this.size = size;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
