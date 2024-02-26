package com.fithub.fithubbackend.domain.trainer.application;

import com.fithub.fithubbackend.domain.trainer.domain.Trainer;
import com.fithub.fithubbackend.domain.trainer.dto.TrainerSpecDto;
import com.fithub.fithubbackend.domain.trainer.repository.TrainerRepository;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {
    private final TrainerRepository trainerRepository;


    @Override
    public TrainerSpecDto getTrainersSpec(Long userId) {
        Trainer trainer = findTrainerByUserId(userId);
        return TrainerSpecDto.builder()
                .trainerCareerList(trainer.getTrainerCareerList())
                .trainerLicenseList(trainer.getTrainerLicenseImgList())
                .build();
    }

    private Trainer findTrainerByUserId (Long userId) {
        return trainerRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.PERMISSION_DENIED, "해당 회원은 트레이너가 아님"));
    }
}
