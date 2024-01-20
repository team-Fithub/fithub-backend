package com.fithub.fithubbackend.domain.board.dto;

import com.fithub.fithubbackend.domain.board.post.domain.Likes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "게시글 좋아요 정보 dto")
public class LikesInfoDto {

    @Schema(description = "게시글 좋아요한 사용자의 닉네임")
    private String nickname;

    @Schema(description = "게시글 좋아요한 사용자의 프로필")
    private String profileUrl;

    @Builder
    public LikesInfoDto(String nickname, String profileUrl) {
        this.nickname = nickname;
        this.profileUrl = profileUrl;
    }

    public static LikesInfoDto toDto(Likes likes) {
        return LikesInfoDto.builder()
                .nickname(likes.getUser().getNickname())
                .profileUrl(likes.getUser().getProfileImg().getUrl())
                .build();
    }

}
