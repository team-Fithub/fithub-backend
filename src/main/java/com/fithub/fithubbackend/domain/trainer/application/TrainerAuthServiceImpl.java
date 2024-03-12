package com.fithub.fithubbackend.domain.trainer.application;

import com.fithub.fithubbackend.domain.trainer.domain.TrainerCareerTemp;
import com.fithub.fithubbackend.domain.trainer.domain.TrainerCertificationRequest;
import com.fithub.fithubbackend.domain.trainer.domain.TrainerExpertiseTemp;
import com.fithub.fithubbackend.domain.trainer.domain.TrainerLicenseTempImg;
import com.fithub.fithubbackend.domain.trainer.dto.TrainerCareerRequestDto;
import com.fithub.fithubbackend.domain.trainer.dto.TrainerCertificationRequestDto;
import com.fithub.fithubbackend.domain.trainer.repository.TrainerCertificationRequestRepository;
import com.fithub.fithubbackend.domain.trainer.repository.TrainerRepository;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.global.common.Category;
import com.fithub.fithubbackend.global.config.s3.AwsS3Uploader;
import com.fithub.fithubbackend.global.domain.Document;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainerAuthServiceImpl implements TrainerAuthService {
    private final TrainerRepository trainerRepository;
    private final TrainerCertificationRequestRepository trainerCertificationRequestRepository;

    private final AwsS3Uploader s3Uploader;

    @Override
    @Transactional
    public void saveTrainerCertificateRequest(TrainerCertificationRequestDto requestDto, User user) {
        if (trainerRepository.existsByUserId(user.getId())) {
            throw new CustomException(ErrorCode.DUPLICATE, "트레이너 인증이 완료된 회원입니다.");
        }

        if (trainerCertificationRequestRepository.existsByRejectedFalseAndUserId(user.getId())) {
            throw new CustomException(ErrorCode.DUPLICATE, "승인 대기 중인 요청이 존재합니다.");
        }

        TrainerCertificationRequest request = TrainerCertificationRequest.builder()
                .user(user).licenseNames(requestDto.getLicenseNames()).build();

        saveTrainerLicenseTempImg(requestDto.getLicenseFileList(), request);
        saveTrainerCareer(requestDto.getCareerList(), request);
        saveTrainerExpertise(requestDto.getSpecialtyList(), request);

        trainerCertificationRequestRepository.save(request);
    }

    private void saveTrainerLicenseTempImg(List<MultipartFile> licenseList, TrainerCertificationRequest request) {
        for (MultipartFile file : licenseList) {
            TrainerLicenseTempImg trainerLicenseTempImg = TrainerLicenseTempImg.builder()
                    .document(createDocument(file)).build();
            trainerLicenseTempImg.updateRequest(request);
        }
    }

    private Document createDocument(MultipartFile file) {
        String path = s3Uploader.imgPath("trainer_license_temp");
        return  Document.builder()
                .inputName(file.getOriginalFilename())
                .url(s3Uploader.putS3(file, path))
                .path(path)
                .build();
    }

    private void saveTrainerCareer(List<TrainerCareerRequestDto> careerList, TrainerCertificationRequest request) {
        for (TrainerCareerRequestDto dto : careerList) {
            Double latitude = dto.getLatitude();
            Double longitude = dto.getLongitude();

            TrainerCareerTemp trainerCareerTemp = TrainerCareerTemp.builder()
                    .dto(dto).point(parsePoint(latitude, longitude)).build();
            trainerCareerTemp.updateRequest(request);
        }
    }

    private Point parsePoint(Double latitude, Double longitude) {
        try {
            return latitude != null && longitude != null ?
                    (Point) new WKTReader().read(String.format("POINT(%s %s)", longitude, latitude))
                    : null;
        } catch (ParseException e) {
            throw new CustomException(ErrorCode.POINT_PARSING_ERROR);
        }
    }

    private void saveTrainerExpertise(List<Category> expertiseList, TrainerCertificationRequest request) {
        for (Category expertise : expertiseList) {
            TrainerExpertiseTemp trainerExpertiseTemp = TrainerExpertiseTemp.builder()
                    .expertise(expertise).build();
            trainerExpertiseTemp.updateRequest(request);
        }
    }

}
