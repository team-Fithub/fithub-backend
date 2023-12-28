package com.fithub.fithubbackend.domain.board.repository;

import com.fithub.fithubbackend.domain.board.comment.domain.Comment;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    void deleteByParentAndPost(Comment comment, Post post);

    Long countByPost(Post post);

}
