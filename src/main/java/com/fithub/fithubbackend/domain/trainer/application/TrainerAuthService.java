package com.fithub.fithubbackend.domain.trainer.application;

import com.fithub.fithubbackend.domain.trainer.dto.TrainerCertificationRequestDto;

public interface TrainerAuthService {
    void saveTrainerCertificateRequest(TrainerCertificationRequestDto request, String email);
}
