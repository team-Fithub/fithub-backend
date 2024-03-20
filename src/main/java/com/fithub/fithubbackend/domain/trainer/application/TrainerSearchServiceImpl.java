package com.fithub.fithubbackend.domain.trainer.application;

import com.fithub.fithubbackend.domain.trainer.domain.Trainer;
import com.fithub.fithubbackend.domain.trainer.dto.TrainerOutlineDto;
import com.fithub.fithubbackend.domain.trainer.dto.TrainerSearchFilterDto;
import com.fithub.fithubbackend.domain.trainer.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class TrainerSearchServiceImpl implements TrainerSearchService {

    private final TrainerRepository trainerRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<TrainerOutlineDto> searchTrainers(TrainerSearchFilterDto dto, Pageable pageable) {
        Page<Trainer> trainers = trainerRepository.searchTrainers(dto, pageable);
        return trainers.map(TrainerOutlineDto::toDto);
    }

}
