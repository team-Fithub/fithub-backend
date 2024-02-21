package com.fithub.fithubbackend.domain.trainer.application;

import com.fithub.fithubbackend.domain.trainer.dto.TrainerCertificationRequestDto;
import com.fithub.fithubbackend.domain.user.domain.User;
import org.locationtech.jts.io.ParseException;

public interface TrainerAuthService {
    void saveTrainerCertificateRequest(TrainerCertificationRequestDto request, User user) throws ParseException;
}
