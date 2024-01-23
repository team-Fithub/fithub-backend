package com.fithub.fithubbackend.domain.board.repository;

import com.fithub.fithubbackend.domain.board.post.domain.Bookmark;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import com.fithub.fithubbackend.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {


    Optional<Bookmark> findByUserAndPost(User user, Post post);

    void deleteByUserAndPost(User user, Post post);

    List<Bookmark> findByUserOrderByCreatedDateDesc(User user);

    boolean existsByUserAndPostId(User user, Long postId);

}
