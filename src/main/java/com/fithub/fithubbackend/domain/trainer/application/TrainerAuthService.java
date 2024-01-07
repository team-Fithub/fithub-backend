package com.fithub.fithubbackend.domain.trainer.application;

import com.fithub.fithubbackend.domain.trainer.dto.TrainerCertificationRequestDto;
import com.fithub.fithubbackend.domain.user.domain.User;

public interface TrainerAuthService {
    void saveTrainerCertificateRequest(TrainerCertificationRequestDto request, User user);
}
