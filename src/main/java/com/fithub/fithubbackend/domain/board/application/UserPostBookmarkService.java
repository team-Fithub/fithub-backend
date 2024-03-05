package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.dto.PostInfoDto;
import com.fithub.fithubbackend.domain.board.post.domain.Bookmark;
import com.fithub.fithubbackend.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;

public interface UserPostBookmarkService {
    void addBookmark(User user, long postId);

    void deleteBookmark(User user, long postId);

    boolean isBookmarked(User user, Long postId);

    Page<PostInfoDto> getBookmarkedPosts(User user, Pageable pageable);

    void deleteAllBookmarksByUser(User user);
}
