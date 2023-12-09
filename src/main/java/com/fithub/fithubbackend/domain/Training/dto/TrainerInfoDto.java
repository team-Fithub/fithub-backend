package com.fithub.fithubbackend.domain.Training.dto;


import com.fithub.fithubbackend.domain.trainer.domain.Trainer;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class TrainerInfoDto {
    private Long trainerId;
    private String name;
    private String trainerProfileImg;
    private String location;

    public static TrainerInfoDto toDto(Trainer trainer) {
        String profile = (trainer.getUser().getProfileImg() != null ? trainer.getUser().getProfileImg().getUrl() : null);

        return TrainerInfoDto.builder()
                .trainerId(trainer.getId())
                .name(trainer.getUser().getName())
                .trainerProfileImg(profile)
                .location(trainer.getLocation())
                .build();
    }
}
