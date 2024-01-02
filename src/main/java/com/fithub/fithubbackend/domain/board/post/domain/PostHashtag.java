package com.fithub.fithubbackend.domain.board.post.domain;


import com.fithub.fithubbackend.global.domain.Hashtag;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "post_hashtag")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostHashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Hashtag hashtag;

    @Builder
    public PostHashtag(Post post, Hashtag hashtag){
        this.post = post;
        this.hashtag = hashtag;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public void setHashtag(Hashtag hashtag) {
        this.hashtag = hashtag;
    }
}
