package com.fithub.fithubbackend.domain.board.repository;

import com.fithub.fithubbackend.domain.board.post.domain.Likes;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import com.fithub.fithubbackend.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface LikesRepository extends JpaRepository<Likes, Long> {

    Optional<Likes> findByUserAndPost(User user, Post post);

    boolean existsByUserAndPostId(User user, Long postId);

    Page<Likes> findByPostId(Pageable pageable, Long postId);

    @Query("select l.post from Likes l where l.user = :user")
    Page<Post> findPostsByUser(@Param("user") User user, Pageable pageable);
    void deleteByUser(User user);
}
