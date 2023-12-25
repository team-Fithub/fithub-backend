package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.post.domain.Post;

public interface PostHashtagService {

    void createPostHashtag(String hashtagStr, Post post);

    void updateHashtag(String hashtagStr, Post post);

}
