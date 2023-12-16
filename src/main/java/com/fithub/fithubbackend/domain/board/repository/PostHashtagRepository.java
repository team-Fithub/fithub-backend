package com.fithub.fithubbackend.domain.board.repository;

import com.fithub.fithubbackend.domain.board.post.domain.Post;
import com.fithub.fithubbackend.domain.board.post.domain.PostHashtag;
import com.fithub.fithubbackend.global.domain.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {

    List<PostHashtag> findByPost(Post post);

    Optional<PostHashtag> findByPostAndHashtag(Post post, Hashtag hashtag);
}
