package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.post.domain.Bookmark;
import com.fithub.fithubbackend.domain.user.domain.User;

import java.util.*;

public interface UserPostBookmarkService {
    void addBookmark(User user, long postId);

    void deleteBookmark(User user, long postId);

    List<Bookmark> getBookmarksByUser(User user);

    boolean isBookmarked(User user, Long postId);
}
