package com.fithub.fithubbackend.domain.Training.application;

import com.fithub.fithubbackend.domain.Training.dto.TrainingLikesInfoDto;
import com.fithub.fithubbackend.domain.user.domain.User;

import java.util.List;

public interface UserTrainingLikeService {
    boolean isLikesTraining(Long trainingId, User user);
    void likesTraining(Long trainingId, User user);
    void cancelTrainingLikes(Long trainingId, User user);

    List<TrainingLikesInfoDto> getTrainingLikesList(User user);
}
