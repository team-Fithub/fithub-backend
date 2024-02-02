package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.dto.*;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import com.fithub.fithubbackend.domain.user.domain.User;

import java.util.List;

public interface UserPostService {

    void createPost(PostCreateDto postCreateDto, User user);

    void updatePost(PostUpdateDto postUpdateDto, User user);

    void deletePost(long postId, User user);
    LikesBookmarkStatusDto checkPostLikeAndBookmarkStatus(User user, long postId);

    List<LikesBookmarkStatusDto> checkPostsLikeAndBookmarkStatus(List<PostRequestDto> postRequestDtos, User user);

    Post getPost(Long postId);
}
