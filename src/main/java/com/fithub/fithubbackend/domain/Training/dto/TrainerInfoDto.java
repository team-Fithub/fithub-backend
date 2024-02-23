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
    private String address;

    public static TrainerInfoDto toDto(Trainer trainer) {
        return TrainerInfoDto.builder()
                .trainerId(trainer.getId())
                .name(trainer.getName())
                .trainerProfileImg(trainer.getProfileUrl())
                .address(trainer.getAddress())
                .build();
    }
}
