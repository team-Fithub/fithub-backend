package com.fithub.fithubbackend.domain.Training.application;

import com.fithub.fithubbackend.domain.Training.dto.TrainingInfoDto;
import com.fithub.fithubbackend.domain.Training.dto.TrainingOutlineDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserTrainingService {
    Page<TrainingOutlineDto> searchAll(Pageable pageable);
    TrainingInfoDto searchById(Long id);

    boolean isLikesTraining(Long trainingId, String email);

    void likesTraining(Long trainingId, boolean likes, String email);
}
