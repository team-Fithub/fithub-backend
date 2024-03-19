package com.fithub.fithubbackend.domain.trainer.application;

import com.fithub.fithubbackend.domain.trainer.dto.TrainerOutlineDto;
import com.fithub.fithubbackend.domain.trainer.dto.TrainerSearchFilterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TrainerSearchService {
    Page<TrainerOutlineDto> searchTrainers(TrainerSearchFilterDto dto, Pageable pageable);
}
