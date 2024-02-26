package com.fithub.fithubbackend.domain.trainer.application;

import com.fithub.fithubbackend.domain.trainer.dto.TrainerSpecDto;

public interface TrainerService {
    TrainerSpecDto getTrainersSpec(Long userId);
}
