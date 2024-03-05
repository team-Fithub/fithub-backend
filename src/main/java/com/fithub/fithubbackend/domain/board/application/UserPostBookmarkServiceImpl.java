package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.dto.PostInfoDto;
import com.fithub.fithubbackend.domain.board.post.domain.Bookmark;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import com.fithub.fithubbackend.domain.board.repository.BookmarkRepository;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserPostBookmarkServiceImpl implements UserPostBookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final PostService postService;

    @Override
    @Transactional
    public void addBookmark(User user, long postId) {

        Post post = postService.getPost(postId);

        Optional<Bookmark> bookmark = bookmarkRepository.findByUserAndPost(user, post);

        if (bookmark.isPresent())
            throw new CustomException(ErrorCode.DUPLICATE, "이미 북마크한 게시글입니다.");

        post.addBookmark(new Bookmark(user, post));
    }

    @Override
    @Transactional
    public void deleteBookmark(User user, long postId) {

        Post post = postService.getPost(postId);

        Optional<Bookmark> bookmark = bookmarkRepository.findByUserAndPost(user, post);

        if (bookmark.isPresent())
            post.getBookmarks().remove(bookmark.get());
    }

    @Override
    public boolean isBookmarked(User user, Long postId) {
        if (bookmarkRepository.existsByUserAndPostId(user, postId))
            return true;
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostInfoDto> getBookmarkedPosts(User user, Pageable pageable) {
        Page<Post> posts = bookmarkRepository.findPostsByUser(user, pageable);
        return posts.map(PostInfoDto::toDto);
    }

    @Override
    @Transactional
    public void deleteAllBookmarksByUser(User user) {
        bookmarkRepository.deleteByUser(user);
    }
}
