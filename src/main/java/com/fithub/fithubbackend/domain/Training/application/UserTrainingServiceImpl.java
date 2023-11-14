package com.fithub.fithubbackend.domain.Training.application;

import com.fithub.fithubbackend.domain.Training.domain.Training;
import com.fithub.fithubbackend.domain.Training.dto.TrainingInfoDto;
import com.fithub.fithubbackend.domain.Training.dto.TrainingOutlineDto;
import com.fithub.fithubbackend.domain.Training.repository.TrainingRepository;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserTrainingServiceImpl implements UserTrainingService {

    private final TrainingRepository trainingRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<TrainingOutlineDto> searchAll(Pageable pageable) {
        Page<Training> trainingPage = trainingRepository.findAll(pageable);
        return trainingPage.map(TrainingOutlineDto::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public TrainingInfoDto searchById(Long id) {
        Training training = trainingRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당하는 트레이닝이 존재하지 않습니다."));
        return TrainingInfoDto.toDto(training);
    }

}
