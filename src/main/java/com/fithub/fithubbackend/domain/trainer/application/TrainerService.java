package com.fithub.fithubbackend.domain.trainer.application;

import com.fithub.fithubbackend.domain.trainer.dto.TrainerCareerDto;
import com.fithub.fithubbackend.domain.trainer.dto.TrainerCareerRequestDto;
import com.fithub.fithubbackend.domain.trainer.dto.TrainerLicenseDto;
import com.fithub.fithubbackend.domain.trainer.dto.TrainerSpecDto;
import org.springframework.web.multipart.MultipartFile;

public interface TrainerService {
    TrainerSpecDto getTrainersSpec(Long userId);

    TrainerCareerDto getTrainerCareer(Long careerId);
    TrainerLicenseDto getTrainerLicenseImg(Long licenseImgId);

    Long createTrainerCareer(Long userId, TrainerCareerRequestDto dto);
    Long createTrainerLicenseImg(Long userId, MultipartFile file);

    void updateTrainerCareer(Long userId, Long careerId, TrainerCareerRequestDto dto);

    void deleteTrainerCareer(Long userId, Long careerId);
}
