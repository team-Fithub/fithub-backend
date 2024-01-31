package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.dto.CommentCreateDto;
import com.fithub.fithubbackend.domain.board.dto.CommentInfoDto;
import com.fithub.fithubbackend.domain.board.dto.ParentCommentInfoDto;
import com.fithub.fithubbackend.domain.board.dto.CommentUpdateDto;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import com.fithub.fithubbackend.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentService {
    Page<ParentCommentInfoDto> getCommentsWithPage(Pageable pageable, long postId);

    void createComment(CommentCreateDto commentCreateDto, User user);

    void updateComment(CommentUpdateDto commentUpdateDto, User user);

    void deleteComment(long commentId, User user);

    long countComment(Post post);

    List<CommentInfoDto> getDetailComments(long commentId);
}
