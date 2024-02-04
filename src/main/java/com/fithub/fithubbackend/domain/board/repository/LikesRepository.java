package com.fithub.fithubbackend.domain.board.repository;

import com.fithub.fithubbackend.domain.board.post.domain.Likes;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import com.fithub.fithubbackend.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface LikesRepository extends JpaRepository<Likes, Long> {

    void deleteByUserAndPost(User user, Post post);

    Optional<Likes> findByUserAndPost(User user, Post post);

    List<Likes> findByUserOrderByCreatedDateDesc(User user);

    List<Likes> findByPostOrderByCreatedDateAsc(Post post);

    boolean existsByUserAndPostId(User user, Long postId);

    Page<Likes> findByPostId(Pageable pageable, Long postId);
}
