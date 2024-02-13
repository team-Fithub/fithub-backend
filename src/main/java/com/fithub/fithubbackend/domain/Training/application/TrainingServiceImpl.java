package com.fithub.fithubbackend.domain.Training.application;

import com.fithub.fithubbackend.domain.Training.domain.Training;
import com.fithub.fithubbackend.domain.Training.domain.TrainingReview;
import com.fithub.fithubbackend.domain.Training.dto.TrainingDocumentDto;
import com.fithub.fithubbackend.domain.Training.dto.TrainingInfoDto;
import com.fithub.fithubbackend.domain.Training.dto.TrainingOutlineDto;
import com.fithub.fithubbackend.domain.Training.dto.TrainingSearchConditionDto;
import com.fithub.fithubbackend.domain.Training.dto.review.TrainingReviewDto;
import com.fithub.fithubbackend.domain.Training.repository.CustomTrainingRepository;
import com.fithub.fithubbackend.domain.Training.repository.TrainingRepository;
import com.fithub.fithubbackend.domain.Training.repository.TrainingReviewRepository;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository trainingRepository;
    private final TrainingReviewRepository trainingReviewRepository;

    private final CustomTrainingRepository customTrainingRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<TrainingOutlineDto> searchAll(Pageable pageable) {
        Page<Training> trainingPage = trainingRepository.findAllByDeletedFalseAndClosedFalse(pageable);
        return trainingPage.map(TrainingOutlineDto::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public TrainingInfoDto searchById(Long id) {
        Training training = trainingRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당하는 트레이닝이 존재하지 않습니다."));
        if (training.isDeleted()) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "삭제된 트레이닝입니다.");
        }

        TrainingInfoDto dto = TrainingInfoDto.toDto(training);
        if (training.getImages() != null && !training.getImages().isEmpty()) {
            List<TrainingDocumentDto> images = training.getImages().stream().map(TrainingDocumentDto::toDto).toList();
            dto.updateImages(images);
        }
        return dto;
    }

    @Override
    public List<TrainingReviewDto> getTrainingReviews(Long id) {
        List<TrainingReview> trainingReviewList = trainingReviewRepository.findByLockedFalseAndTrainingId(id);
        return trainingReviewList.stream().map(TrainingReviewDto::toDto).toList();
    }

    @Override
    public Page<TrainingOutlineDto> searchTrainingByConditions(TrainingSearchConditionDto conditions, Pageable pageable) {
        Page<Training> trainingList = customTrainingRepository.searchByConditions(conditions, pageable);
        return trainingList.map(TrainingOutlineDto::toDto);
    }
}
