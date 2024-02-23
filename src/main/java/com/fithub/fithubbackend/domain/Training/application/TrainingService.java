package com.fithub.fithubbackend.domain.Training.application;

import com.fithub.fithubbackend.domain.Training.dto.TrainingInfoDto;
import com.fithub.fithubbackend.domain.Training.dto.TrainingOutlineDto;
import com.fithub.fithubbackend.domain.Training.dto.TrainingSearchConditionDto;
import com.fithub.fithubbackend.domain.Training.dto.review.TrainingReviewDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TrainingService {
    Page<TrainingOutlineDto> searchAll(Pageable pageable);
    TrainingInfoDto searchById(Long id);
    List<TrainingReviewDto> getTrainingReviews(Long id);

    Page<TrainingOutlineDto> searchTrainingByConditions(TrainingSearchConditionDto conditions, Pageable pageable);
    List<TrainingOutlineDto> searchTrainingByLocation(Double latitude, Double longitude);
}
