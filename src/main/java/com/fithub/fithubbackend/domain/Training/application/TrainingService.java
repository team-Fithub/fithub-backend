package com.fithub.fithubbackend.domain.Training.application;

import com.fithub.fithubbackend.domain.Training.dto.TrainingCreateDto;

public interface TrainingService {
    Long createTraining(TrainingCreateDto dto, Long trainerId);
    Long updateTraining(TrainingCreateDto dto, Long trainingId);
    void deleteTraining(Long id);

    void updateClosed(Long id, boolean closed);
}
