package com.fithub.fithubbackend.domain.board.repository;

import com.fithub.fithubbackend.domain.board.post.domain.Bookmark;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import com.fithub.fithubbackend.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByUserAndPost(User user, Post post);
    boolean existsByUserAndPostId(User user, Long postId);
    @Query("select b.post from Bookmark b where b.user = :user")
    Page<Post> findPostsByUser(@Param("user") User user, Pageable pageable);
    void deleteByUser(User user);
}
