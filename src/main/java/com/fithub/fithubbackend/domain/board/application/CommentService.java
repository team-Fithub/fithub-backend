package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.dto.CommentCreateDto;
import com.fithub.fithubbackend.domain.board.dto.CommentInfoDto;
import com.fithub.fithubbackend.domain.board.dto.CommentUpdateDto;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface CommentService {
    void createComment(CommentCreateDto commentCreateDto, UserDetails userDetails);

    void updateComment(CommentUpdateDto commentUpdateDto, UserDetails userDetails);

    void deleteComment(long commentId, UserDetails userDetails);

    long countComment(Post post);

    List<CommentInfoDto> getComments(Post post);
}
