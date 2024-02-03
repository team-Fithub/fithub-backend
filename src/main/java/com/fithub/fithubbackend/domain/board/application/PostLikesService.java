package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.dto.likes.LikesInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostLikesService {

    Page<LikesInfoDto> getAllLikedUsersForPostDetail(Pageable pageable, Long postId);
}
