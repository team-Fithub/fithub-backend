package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.fithub.fithubbackend.domain.user.domain.User;

import java.util.*;

public interface PostService {
    void createPost(PostCreateDto postCreateDto, User user);

    void updatePost(PostUpdateDto postUpdateDto, User user);

    void likesPost(long postId, User user);

    void createBookmark(long postId, User user);

    void notLikesPost(long postId, User user);

    void deleteBookmark(long postId, User user);

    void deletePost(long postId, User user);

    Page<PostOutlineDto> getAllPosts(Pageable pageable);

    PostDetailInfoDto getPostDetail(long postId);

    LikesBookmarkStatusDto checkPostLikeAndBookmarkStatus(User user, long postId);

    List<LikesBookmarkStatusDto> checkPostsLikeAndBookmarkStatus(List<PostOutlineDto> postOutlineDtos, User user);
}
