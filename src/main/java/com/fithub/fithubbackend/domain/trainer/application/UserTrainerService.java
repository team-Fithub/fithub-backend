package com.fithub.fithubbackend.domain.trainer.application;

import com.fithub.fithubbackend.domain.Training.dto.Location;
import com.fithub.fithubbackend.domain.trainer.dto.TrainerRecommendationOutlineDto;
import com.fithub.fithubbackend.domain.user.domain.User;

public interface UserTrainerService {
    TrainerRecommendationOutlineDto recommendTrainers(User user, Location location, int size);
}