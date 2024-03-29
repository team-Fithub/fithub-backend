package com.fithub.fithubbackend.domain.board.repository;

import com.fithub.fithubbackend.domain.board.post.domain.Post;
import com.fithub.fithubbackend.domain.board.post.domain.PostHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {

    @Query(value = "SELECT h.content FROM PostHashtag p JOIN FETCH Hashtag h ON p.hashtag = h WHERE p.post.id = :postId order by p.id")
    List<String> findHashtagByPostId(@Param("postId") Long postId);

    @Query(value = "SELECT ph FROM PostHashtag ph LEFT JOIN FETCH ph.hashtag WHERE ph.post.id = :postId")
    List<PostHashtag> findByPostFetch(@Param("postId") Long postId);

}
