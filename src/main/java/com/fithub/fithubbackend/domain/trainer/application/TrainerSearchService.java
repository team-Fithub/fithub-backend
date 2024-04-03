package com.fithub.fithubbackend.domain.trainer.application;

import com.fithub.fithubbackend.domain.trainer.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TrainerSearchService {
    Page<TrainerOutlineDto> searchTrainers(TrainerSearchFilterDto dto, Pageable pageable);

    TrainerSearchAllReviewDto searchTrainerReviews(Long trainerId);

    TrainerSearchAllLicenseDto searchTrainerLicenses(Long trainerId);

    TrainerSearchCompanyDto searchTrainerCompanyInfo(Long trainerId);

    TrainerOutlineDto searchTrainerProfileInfo(Long trainerId);
}
