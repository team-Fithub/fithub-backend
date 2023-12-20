package com.fithub.fithubbackend.domain.admin.application;

import com.fithub.fithubbackend.domain.trainer.domain.Trainer;
import com.fithub.fithubbackend.domain.trainer.domain.TrainerCareer;
import com.fithub.fithubbackend.domain.trainer.domain.TrainerCertificationRequest;
import com.fithub.fithubbackend.domain.trainer.domain.TrainerLicenseImg;
import com.fithub.fithubbackend.domain.trainer.repository.TrainerCertificationRequestRepository;
import com.fithub.fithubbackend.domain.trainer.repository.TrainerRepository;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final TrainerCertificationRequestRepository trainerCertificationRequestRepository;
    private final TrainerRepository trainerRepository;

    @Override
    @Transactional
    public void acceptTrainerCertificateRequest(Long requestId) {
        TrainerCertificationRequest trainerCertificationRequest = trainerCertificationRequestRepository.findById(requestId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "트레이너 인증 요청을 찾을 수 없습니다."));
        Trainer trainer = Trainer.builder()
                .user(trainerCertificationRequest.getUser())
                .build();

        List<TrainerCareer> trainerCareerList = trainerCertificationRequest.getCareerTempList().stream().map(c -> TrainerCareer.builder()
                        .trainer(trainer)
                        .trainerCareerTemp(c)
                        .build())
                .toList();

        List<TrainerLicenseImg> trainerLicenseImgList = trainerCertificationRequest.getLicenseTempImgList().stream().map(img -> TrainerLicenseImg.builder()
                        .trainer(trainer)
                        .document(img.getDocument())
                        .build())
                .toList();

        trainer.updateCareerList(trainerCareerList);
        trainer.updateTrainerLicenseImg(trainerLicenseImgList);

        TrainerCareer career = trainerCareerList.stream().filter(TrainerCareer::isWorking).findFirst().orElseGet(null);
        if (career != null) {
            trainer.updateLocation(career.getCompany());
        }

        trainerRepository.save(trainer);
        trainerCertificationRequestRepository.delete(trainerCertificationRequest);
    }
}
