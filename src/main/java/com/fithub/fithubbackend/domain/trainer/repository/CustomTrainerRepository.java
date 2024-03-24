package com.fithub.fithubbackend.domain.trainer.repository;

import com.fithub.fithubbackend.domain.trainer.domain.Trainer;
import com.fithub.fithubbackend.domain.trainer.dto.TrainerSearchFilterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomTrainerRepository {
    Page<Trainer> searchTrainers(TrainerSearchFilterDto dto, Pageable pageable);

}
