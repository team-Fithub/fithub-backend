package com.fithub.fithubbackend.domain.board.dto;

import com.fithub.fithubbackend.domain.board.post.domain.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "게시글 세부 정보 dto")
public class PostDetailInfoDto {

    @Schema(description = "게시글 정보")
    private PostOutlineDto postOutlineDto;

    @Schema(description = "게시글 댓글 리스트")
    private List<CommentInfoDto> postComments;

    public void setComment(List<CommentInfoDto> comments) {
        this.postComments = comments;
    }

    @Builder
    public PostDetailInfoDto(PostOutlineDto postOutlineDto) {
        this.postOutlineDto = postOutlineDto;
    }

    public static PostDetailInfoDto toDto(Post post) {
        return PostDetailInfoDto.builder()
                .postOutlineDto(PostOutlineDto.toDto(post))
                .build();
    }
}
