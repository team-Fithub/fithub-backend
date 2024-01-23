package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.post.domain.Likes;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import com.fithub.fithubbackend.domain.board.repository.LikesRepository;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikesServiceImpl implements LikesService {

    private final LikesRepository likesRepository;
    @Override
    @Transactional
    public void addLikes(User user, Post post) {

        Optional<Likes> likes = likesRepository.findByUserAndPost(user, post);
        if (likes.isPresent())
            throw new CustomException(ErrorCode.DUPLICATE, "이미 좋아요한 게시글입니다.");

        post.addLikes(new Likes(user, post));
    }

    @Override
    @Transactional
    public void deleteLikes(User user, Post post) {
        Optional<Likes> likes = likesRepository.findByUserAndPost(user, post);
        if (likes.isPresent())
            post.getLikes().remove(likes.get());
    }

    @Override
    @Transactional
    public List<Likes> getLikesByPost(Post post) {
        return likesRepository.findByPostOrderByCreatedDateAsc(post);
    }

    @Override
    @Transactional
    public List<Likes> getLikesByUser(User user) {
        return likesRepository.findByUserOrderByCreatedDateDesc(user);
    }

    @Override
    public boolean isLiked(User user, Long postId) {
        if (likesRepository.existsByUserAndPostId(user, postId))
            return true;
        return false;
    }
}
