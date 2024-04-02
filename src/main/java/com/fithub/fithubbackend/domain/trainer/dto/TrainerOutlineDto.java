package com.fithub.fithubbackend.domain.trainer.dto;

import com.fithub.fithubbackend.domain.trainer.domain.Trainer;
import com.fithub.fithubbackend.domain.user.domain.UserInterest;
import com.fithub.fithubbackend.global.common.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Setter @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TrainerOutlineDto {

    @Schema(description = "현재 일하는 장소")
    private String address;

    @Schema(description = "트레이너 id")
    private long id;

    @Schema(description = "트레이너 소개")
    private String bio;

    @Schema(description = "트레이너 이름")
    private String name;

    @Schema(description = "트레이너 이메일")
    private String email;

    @Schema(description = "트레이너 프로필 이미지")
    private String profileUrl;

    @Schema(description = "트레이너 전문 분야")
    private List<Category> interests;

    @Builder
    public TrainerOutlineDto(long id, String bio, String address, String name, String email,
                             String profileUrl, List<Category> interests) {
        this.id = id;
        this.bio = bio;
        this.address = address;
        this.name = name;
        this.email = email;
        this.profileUrl = profileUrl;
        this.interests = interests;
    }

    public static TrainerOutlineDto toDto(Trainer trainer) {
        return TrainerOutlineDto.builder()
                .id(trainer.getId())
                .bio(trainer.getUser().getBio())
                .address(trainer.getAddress())
                .name(trainer.getName())
                .email(trainer.getEmail())
                .profileUrl(trainer.getProfileUrl())
                .interests(trainer.getUser().getInterests().stream().map(UserInterest::getInterest).toList())
                .build();
    }

}
