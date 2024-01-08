package com.fithub.fithubbackend.domain.board.repository;

import com.fithub.fithubbackend.domain.board.comment.domain.Comment;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    void deleteByParentAndPost(Comment comment, Post post);

    Long countByPostAndDeletedIsNull(Post post);

    @Query(value = "SELECT c  FROM Comment c  LEFT JOIN FETCH Comment pc ON c.parent = pc " +
            "WHERE c.post = :post ORDER BY COALESCE(pc.id, 0) ASC, c.createdDate ASC")
    List<Comment> findByPostWithFetch(@Param("post") Post post);
}
