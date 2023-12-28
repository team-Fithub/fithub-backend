package com.fithub.fithubbackend.domain.board.repository;

import com.fithub.fithubbackend.domain.board.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
