package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.dto.PostInfoDto;
import com.fithub.fithubbackend.domain.board.post.domain.Likes;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import com.fithub.fithubbackend.domain.board.repository.LikesRepository;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserPostLikesServiceImpl implements UserPostLikesService {

    private final LikesRepository likesRepository;
    private final PostService postService;

    // TODO 게시글 좋아요 시, 게시글 작성자에게 알림 전송
    @Override
    @Transactional
    public void addLikes(User user, long postId) {

        Post post = postService.getPost(postId);
        Optional<Likes> likes = likesRepository.findByUserAndPost(user, post);
        if (likes.isPresent())
            throw new CustomException(ErrorCode.DUPLICATE, "이미 좋아요한 게시글입니다.");

        post.addLikes(new Likes(user, post));
    }

    @Override
    @Transactional
    public void deleteLikes(User user, long postId) {

        Post post = postService.getPost(postId);
        Optional<Likes> likes = likesRepository.findByUserAndPost(user, post);
        if (likes.isPresent())
            post.getLikes().remove(likes.get());
    }

    @Override
    public boolean isLiked(User user, Long postId) {
        if (likesRepository.existsByUserAndPostId(user, postId))
            return true;
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostInfoDto> getLikedPosts(User user, Pageable pageable) {
        Page<Post> posts = likesRepository.findPostsByUser(user, pageable);
        return posts.map(PostInfoDto::toDto);
    }

}
