package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.dto.comment.CommentCreateDto;
import com.fithub.fithubbackend.domain.board.dto.comment.CommentUpdateDto;
import com.fithub.fithubbackend.domain.user.domain.User;

public interface UserCommentService {

    void createComment(CommentCreateDto commentCreateDto, User user);

    void updateComment(CommentUpdateDto commentUpdateDto, User user);

    void deleteComment(long commentId, User user);
}
