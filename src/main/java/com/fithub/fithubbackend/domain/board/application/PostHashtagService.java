package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.post.domain.Post;

public interface PostHashtagService {
    void createOrUpdateHashtag(String hashtagStr, Post post);

    void updateHashTag(String hashTags, Post post);
}
