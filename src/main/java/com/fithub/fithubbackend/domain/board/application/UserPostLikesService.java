package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.dto.PostInfoDto;
import com.fithub.fithubbackend.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserPostLikesService {
    void addLikes(User user, long postId);

    void deleteLikes(User user, long postId);

    boolean isLiked(User user, Long postId);
    Page<PostInfoDto> getLikedPosts(User user, Pageable pageable);
}
