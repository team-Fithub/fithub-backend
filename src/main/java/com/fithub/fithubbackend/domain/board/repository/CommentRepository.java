package com.fithub.fithubbackend.domain.board.repository;

import com.fithub.fithubbackend.domain.board.comment.domain.Comment;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    void deleteByParentAndPost(Comment comment, Post post);

    Long countByPostAndDeletedIsNull(Post post);

    Page<Comment> findByPostIdAndParentIsNull(Pageable pageable, long postId);

    List<Comment> findByParent(Comment comment);
}
