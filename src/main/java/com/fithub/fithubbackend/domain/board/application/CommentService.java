package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.dto.CommentCreateDto;
import com.fithub.fithubbackend.domain.board.dto.CommentUpdateDto;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import com.fithub.fithubbackend.domain.user.domain.User;

public interface CommentService {
    void createComment(CommentCreateDto commentCreateDto, User user);

    void updateComment(CommentUpdateDto commentUpdateDto, User user);

    void deleteComment(long commentId, User user);

    long countComment(Post post);

    void deleteComments(Post post);
}
