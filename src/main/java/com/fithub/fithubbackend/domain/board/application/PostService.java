package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.dto.PostCreateDto;
import com.fithub.fithubbackend.domain.board.dto.PostUpdateDto;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

public interface PostService {
    void createPost(PostCreateDto postCreateDto, UserDetails userDetails);

    void updatePost(PostUpdateDto postUpdateDto, UserDetails userDetails);

    void likesPost(long postId, UserDetails userDetails);

    void createBookmark(long postId, UserDetails userDetails);

    void notLikesPost(long postId, UserDetails userDetails);

    void deleteBookmark(long postId, UserDetails userDetails);

    void deletePost(long postId, UserDetails userDetails);
}
