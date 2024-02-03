package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.dto.*;
import com.fithub.fithubbackend.domain.board.dto.likes.LikedUsersInfoDto;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface PostService {
    Page<PostInfoDto> getAllPosts(Pageable pageable);

    PostInfoDto getPostDetail(long postId);

    Post getPost(Long postId);

    LikedUsersInfoDto getLikedUsersForPostDetail(Long postId);

    List<LikedUsersInfoDto> getLikedUsersForPosts(List<PostRequestDto> postRequestDtos);

}
