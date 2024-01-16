package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.dto.PostCreateDto;
import com.fithub.fithubbackend.domain.board.dto.PostDetailInfoDto;
import com.fithub.fithubbackend.domain.board.dto.PostInfoDto;
import com.fithub.fithubbackend.domain.board.dto.PostUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.fithub.fithubbackend.domain.user.domain.User;


public interface PostService {
    void createPost(PostCreateDto postCreateDto, User user);

    void updatePost(PostUpdateDto postUpdateDto, User user);

    void likesPost(long postId, User user);

    void createBookmark(long postId, User user);

    void notLikesPost(long postId, User user);

    void deleteBookmark(long postId, User user);

    void deletePost(long postId, User user);

    Page<PostInfoDto> getAllPostsForUser(Pageable pageable, User use);

    PostDetailInfoDto getPostDetailForUser(long postId, User use);

    Page<PostInfoDto> getAllPostsForNonUser(Pageable pageable);

    PostDetailInfoDto getPostDetailForNonUser(long postId);
}
