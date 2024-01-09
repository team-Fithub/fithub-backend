package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.dto.CommentCreateDto;
import com.fithub.fithubbackend.domain.board.dto.CommentInfoDto;
import com.fithub.fithubbackend.domain.board.dto.CommentUpdateDto;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import com.fithub.fithubbackend.domain.user.domain.User;

import java.util.List;

public interface CommentService {
    void createComment(CommentCreateDto commentCreateDto, User user);

    void updateComment(CommentUpdateDto commentUpdateDto, User user);

    void deleteComment(long commentId, User user);

    long countComment(Post post);

    List<CommentInfoDto> getComments(Post post);

    List<CommentInfoDto> getCommentsVer2(Post post);
}
