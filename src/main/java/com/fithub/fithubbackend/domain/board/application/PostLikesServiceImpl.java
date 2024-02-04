package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.dto.likes.LikesInfoDto;
import com.fithub.fithubbackend.domain.board.post.domain.Likes;
import com.fithub.fithubbackend.domain.board.repository.LikesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostLikesServiceImpl implements PostLikesService {

    private final LikesRepository likesRepository;

    @Override
    @Transactional
    public Page<LikesInfoDto> getAllLikedUsersForPostDetail(Pageable pageable, Long postId) {

        Page<Likes> likes = likesRepository.findByPostId(pageable, postId);
        return likes.map(LikesInfoDto::toDto);
    }


}
