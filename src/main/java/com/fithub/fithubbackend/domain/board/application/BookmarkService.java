package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.post.domain.Bookmark;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import com.fithub.fithubbackend.domain.user.domain.User;

import java.util.*;

public interface BookmarkService {
    void addBookmark(User user, Post post);

    void deleteBookmark(User user, Post post);

    List<Bookmark> getBookmarksByUser(User user);

    boolean isBookmarked(User user, Long postId);
}
