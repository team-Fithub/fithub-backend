package com.fithub.fithubbackend.domain.board.dto;

import com.fithub.fithubbackend.domain.board.post.domain.Likes;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LikesInfoDto {

    private String likedUser;

    private String likedUserProfileUrl;

    private String likedUserBio;

    public static LikesInfoDto fromEntity(Likes likes) {
        return LikesInfoDto.builder()
                .likedUser(likes.getUser().getNickname())
                .likedUserProfileUrl(likes.getUser().getProfileImg().getUrl())
                .likedUserBio(likes.getUser().getBio()).build();
    }

}
