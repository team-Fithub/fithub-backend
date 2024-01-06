package com.fithub.fithubbackend.domain.trainer.application;

import com.fithub.fithubbackend.domain.trainer.domain.TrainerCareerTemp;
import com.fithub.fithubbackend.domain.trainer.domain.TrainerCertificationRequest;
import com.fithub.fithubbackend.domain.trainer.domain.TrainerLicenseTempImg;
import com.fithub.fithubbackend.domain.trainer.dto.TrainerCareerRequestDto;
import com.fithub.fithubbackend.domain.trainer.dto.TrainerCertificationRequestDto;
import com.fithub.fithubbackend.domain.trainer.repository.TrainerCertificationRequestRepository;
import com.fithub.fithubbackend.domain.trainer.repository.TrainerRepository;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.domain.user.repository.UserRepository;
import com.fithub.fithubbackend.global.config.s3.AwsS3Uploader;
import com.fithub.fithubbackend.global.domain.Document;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainerAuthServiceImpl implements TrainerAuthService {
    private final TrainerRepository trainerRepository;
    private final TrainerCertificationRequestRepository trainerCertificationRequestRepository;

    private final UserRepository userRepository;

    private final AwsS3Uploader s3Uploader;

    @Override
    public void saveTrainerCertificateRequest(TrainerCertificationRequestDto requestDto, User user) {
        if (trainerRepository.existsByUserId(user.getId())) {
            throw new CustomException(ErrorCode.DUPLICATE, "트레이너 인증이 완료된 회원입니다.");
        }

        if (trainerCertificationRequestRepository.existsByRejectedFalseAndUserId(user.getId())) {
            throw new CustomException(ErrorCode.DUPLICATE, "반려되지 않은 트레이너 인증 요청이 존재하는 회원입니다.");
        }

        TrainerCertificationRequest trainerCertificationRequest = TrainerCertificationRequest.builder()
                .user(user).licenseNames(requestDto.getLicenseNames()).build();

        List<MultipartFile> licenseFileList = requestDto.getLicenseFileList();
        if (licenseFileList != null && !licenseFileList.isEmpty()) {
            for (MultipartFile file : licenseFileList) {
                String path = s3Uploader.imgPath("trainer_license_temp");
                Document document = Document.builder()
                        .inputName(file.getOriginalFilename())
                        .url(s3Uploader.putS3(file, path))
                        .path(path)
                        .build();
                TrainerLicenseTempImg trainerLicenseTempImg = TrainerLicenseTempImg.builder()
                        .document(document).build();
                trainerLicenseTempImg.updateRequest(trainerCertificationRequest);
            }
        }

        List<TrainerCareerRequestDto> careerList = requestDto.getCareerList();
        for (TrainerCareerRequestDto dto : careerList) {
            TrainerCareerTemp trainerCareerTemp = TrainerCareerTemp.builder()
                    .dto(dto).build();
            trainerCareerTemp.updateRequest(trainerCertificationRequest);
        }

        trainerCertificationRequestRepository.save(trainerCertificationRequest);
    }
}
