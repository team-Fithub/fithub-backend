package com.fithub.fithubbackend.domain.board.repository;

import com.fithub.fithubbackend.domain.board.post.domain.PostHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {

    @Query(value = "SELECT h.content FROM PostHashtag p JOIN Hashtag h ON p.hashtag = h WHERE p.post.id = :postId order by p.id")
    List<String> findHashtagByPostId(@Param("postId") Long postId);
}
