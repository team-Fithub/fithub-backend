package com.fithub.fithubbackend.domain.Training.application;

import com.fithub.fithubbackend.domain.Training.dto.TrainingCreateDto;
import com.fithub.fithubbackend.domain.user.domain.User;

public interface TrainingService {
    Long createTraining(TrainingCreateDto dto, User user);
    Long updateTraining(TrainingCreateDto dto, Long trainingId, User user);
    void deleteTraining(Long id);

    void updateClosed(Long id, boolean closed, User user);
}
