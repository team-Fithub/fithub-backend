package com.fithub.fithubbackend.domain.admin.application;

import com.fithub.fithubbackend.domain.admin.domain.TrainerCertificationRejectLog;
import com.fithub.fithubbackend.domain.admin.dto.CertRejectDto;
import com.fithub.fithubbackend.domain.admin.repository.TrainerCareerTempRepository;
import com.fithub.fithubbackend.domain.admin.repository.TrainerCertificationRejectLogRepository;
import com.fithub.fithubbackend.domain.admin.repository.TrainerLicenseTempImgRepository;
import com.fithub.fithubbackend.domain.trainer.domain.*;
import com.fithub.fithubbackend.domain.trainer.repository.TrainerCertificationRequestRepository;
import com.fithub.fithubbackend.domain.trainer.repository.TrainerRepository;
import com.fithub.fithubbackend.domain.user.repository.DocumentRepository;
import com.fithub.fithubbackend.global.config.s3.AwsS3Uploader;
import com.fithub.fithubbackend.global.domain.Document;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final TrainerCertificationRequestRepository trainerCertificationRequestRepository;
    private final TrainerCareerTempRepository trainerCareerTempRepository;
    private final TrainerLicenseTempImgRepository trainerLicenseTempImgRepository;
    private final DocumentRepository documentRepository;

    private final TrainerRepository trainerRepository;

    private final TrainerCertificationRejectLogRepository rejectLogRepository;

    private final AwsS3Uploader awsS3Uploader;

    @Override
    @Transactional
    // TODO: 승인됐다는 알림 보내기?
    public void acceptTrainerCertificateRequest(Long requestId) {
        TrainerCertificationRequest request = findCertRequestById(requestId);
        Trainer trainer = Trainer.builder().user(request.getUser()).build();

        convertTempCareerIntoTrainerCareer(request.getCareerTempList(), trainer);
        convertTempLicenseIntoTrainerLicense(request.getLicenseTempImgList(), trainer);

        trainer.grantPermission();
        trainerRepository.save(trainer);
        trainerCertificationRequestRepository.delete(request);
    }

    private void convertTempCareerIntoTrainerCareer(List<TrainerCareerTemp> temp, Trainer trainer) {
        List<TrainerCareer> list = temp.stream()
                .map(t -> {
                    TrainerCareer career = TrainerCareer.builder().trainer(trainer).trainerCareerTemp(t).build();
                    if (career.isWorking()) {
                        trainer.updateAddress(career);
                    }
                    return career;
                })
                .toList();

        trainer.updateCareerList(list);
    }

    private void convertTempLicenseIntoTrainerLicense(List<TrainerLicenseTempImg> temp, Trainer trainer) {
        List<TrainerLicenseImg> list = temp.stream().map(img -> TrainerLicenseImg.builder().trainer(trainer).document(img.getDocument()).build())
                .toList();
        trainer.updateTrainerLicenseImg(list);
    }

    @Override
    @Transactional
    // TODO: 반려됐다는 알림 보내기?
    public void rejectTrainerCertificateRequest(Long requestId, CertRejectDto dto) {
        TrainerCertificationRequest request = findCertRequestById(requestId);

        deleteTempData(request);
        request.requestReject();

        trainerCertificationRequestRepository.saveAndFlush(request);

        createRejectLog(request, dto);
    }

    private void deleteTempData(TrainerCertificationRequest request) {
        List<Document> documentToDeleted = getDocumentToDeleted(request.getLicenseTempImgList());

        trainerCareerTempRepository.deleteAll(request.getCareerTempList());
        trainerLicenseTempImgRepository.deleteAll(request.getLicenseTempImgList());
        documentRepository.deleteAll(documentToDeleted);
    }

    private List<Document> getDocumentToDeleted(List<TrainerLicenseTempImg> tempImgList) {
        List<Document> documentToDeleted = new ArrayList<>();
        for (TrainerLicenseTempImg trainerLicenseTempImg : tempImgList) {
            Document document = trainerLicenseTempImg.getDocument();
            documentToDeleted.add(document);
            awsS3Uploader.deleteS3(document.getPath());
        }
        return documentToDeleted;
    }

    private void createRejectLog(TrainerCertificationRequest request, CertRejectDto dto) {
        TrainerCertificationRejectLog rejectLog = TrainerCertificationRejectLog.builder()
                .trainerCertificationRequest(request)
                .dto(dto)
                .build();
        rejectLogRepository.save(rejectLog);
    }

    private TrainerCertificationRequest findCertRequestById(Long requestId) {
        return trainerCertificationRequestRepository.findById(requestId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "트레이너 인증 요청을 찾을 수 없습니다."));
    }
}
