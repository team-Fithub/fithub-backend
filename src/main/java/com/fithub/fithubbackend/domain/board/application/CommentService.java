package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.dto.comment.CommentInfoDto;
import com.fithub.fithubbackend.domain.board.dto.comment.ParentCommentInfoDto;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentService {
    Page<ParentCommentInfoDto> getCommentsWithPage(Pageable pageable, long postId);

    long countComment(Post post);

    long countCommentByPostId(long postId);

    List<CommentInfoDto> getDetailComments(long commentId);
}
