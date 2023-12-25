package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.dto.PostCreateDto;
import com.fithub.fithubbackend.domain.board.dto.PostUpdateDto;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

public interface PostService {
    void createPost(PostCreateDto postCreateDto, UserDetails userDetails) throws IOException;

    void updatePost(PostUpdateDto postUpdateDto, UserDetails userDetails) throws IOException;

}
