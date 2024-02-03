package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.post.domain.Likes;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import com.fithub.fithubbackend.domain.user.domain.User;

import java.util.*;

public interface UserPostLikesService {
    void addLikes(User user, long postId);

    void deleteLikes(User user, long postId);

    List<Likes> getLikesByPost(Post post);

    List<Likes> getLikesByUser(User user);

    boolean isLiked(User user, Long postId);
}
