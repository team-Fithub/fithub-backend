package com.fithub.fithubbackend.domain.board.dto;

import com.fithub.fithubbackend.domain.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "게시글 작성자 정보 dto")
public class PostWriterInfoDto {

    @Schema(description = "작성자 닉네임")
    private String nickname;

    @Schema(description = "게시글 작성자 프로필 url")
    private String profileUrl;

    @Builder
    public PostWriterInfoDto(String nickname, String profileUrl) {
        this.nickname = nickname;
        this.profileUrl = profileUrl;
    }

    public static PostWriterInfoDto toDto(User user) {
        return PostWriterInfoDto.builder()
                .nickname(user.getNickname())
                .profileUrl(user.getProfileImg().getUrl())
                .build();
    }
}
