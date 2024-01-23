package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.post.domain.Bookmark;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import com.fithub.fithubbackend.domain.board.repository.BookmarkRepository;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;

    @Override
    @Transactional
    public void addBookmark(User user, Post post) {

        Optional<Bookmark> bookmark = bookmarkRepository.findByUserAndPost(user, post);

        if (bookmark.isPresent())
            throw new CustomException(ErrorCode.DUPLICATE, "이미 북마크한 게시글입니다.");

        post.addBookmark(new Bookmark(user, post));
    }

    @Override
    @Transactional
    public void deleteBookmark(User user, Post post) {
        Optional<Bookmark> bookmark = bookmarkRepository.findByUserAndPost(user, post);

        if (bookmark.isPresent())
            post.getBookmarks().remove(bookmark.get());
    }


    @Override
    @Description("회원의 북마크 기록 조회 (내림차순)")
    @Transactional
    public List<Bookmark> getBookmarksByUser(User user) {
        return bookmarkRepository.findByUserOrderByCreatedDateDesc(user);
    }

    @Override
    public boolean isBookmarked(User user, Long postId) {
        if (bookmarkRepository.existsByUserAndPostId(user, postId))
            return true;
        return false;
    }
}
