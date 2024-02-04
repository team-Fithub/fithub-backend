package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.dto.PostInfoDto;
import com.fithub.fithubbackend.domain.board.dto.comment.CommentCreateDto;
import com.fithub.fithubbackend.domain.board.dto.comment.CommentUpdateDto;
import com.fithub.fithubbackend.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserCommentService {

    void createComment(CommentCreateDto commentCreateDto, User user);

    void updateComment(CommentUpdateDto commentUpdateDto, User user);

    void deleteComment(long commentId, User user);

    Page<PostInfoDto> getPostsByUserAndComments(Pageable pageable, User user);

}
