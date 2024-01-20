package com.fithub.fithubbackend.domain.board.dto;

import com.fithub.fithubbackend.domain.board.post.domain.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Schema(description = "게시글 좋아요 정보 dto")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LikedUsersInfoDto {

    @Schema(description = "게시글 좋아요 수")
    private Long likedCount;

    @Schema(description = "게시글 좋아요 리스트")
    private List<LikesInfoDto> likedUsers;

    @Builder
    public LikedUsersInfoDto(Long likedCount, List<LikesInfoDto> likedUsers) {
        this.likedCount = likedCount;
        this.likedUsers = likedUsers;
    }

    public static LikedUsersInfoDto toDo(Post post) {
        return LikedUsersInfoDto.builder()
                .likedCount((long) post.getLikes().size())
                .likedUsers(post.getLikes().stream().limit(3).map(LikesInfoDto::toDto).collect(Collectors.toList()))
                .build();
    }

}

