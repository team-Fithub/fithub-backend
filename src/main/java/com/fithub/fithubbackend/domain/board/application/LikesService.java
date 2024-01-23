package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.post.domain.Likes;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import com.fithub.fithubbackend.domain.user.domain.User;

import java.util.*;

public interface LikesService {
    void addLikes(User user, Post post);

    void deleteLikes(User user, Post post);

    List<Likes> getLikesByPost(Post post);

    List<Likes> getLikesByUser(User user);

    boolean isLiked(User user, Long postId);
}
