package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.dto.PostCreateDto;
import com.fithub.fithubbackend.domain.board.dto.PostInfoDto;
import com.fithub.fithubbackend.domain.board.dto.PostUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;


public interface PostService {
    void createPost(PostCreateDto postCreateDto, UserDetails userDetails);

    void updatePost(PostUpdateDto postUpdateDto, UserDetails userDetails);

    void likesPost(long postId, UserDetails userDetails);

    void createBookmark(long postId, UserDetails userDetails);

    void notLikesPost(long postId, UserDetails userDetails);

    void deleteBookmark(long postId, UserDetails userDetails);

    void deletePost(long postId, UserDetails userDetails);

    Page<PostInfoDto> getAllPosts(Pageable pageable, UserDetails userDetails);

    PostInfoDto getPostDetail(long postId, UserDetails userDetails);

}
