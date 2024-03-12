package com.fithub.fithubbackend.domain.trainer.application;

import com.fithub.fithubbackend.domain.Training.domain.Training;
import com.fithub.fithubbackend.domain.Training.repository.TrainingRepository;
import com.fithub.fithubbackend.domain.trainer.domain.Trainer;
import com.fithub.fithubbackend.domain.trainer.domain.TrainerCareer;
import com.fithub.fithubbackend.domain.trainer.domain.TrainerLicenseImg;
import com.fithub.fithubbackend.domain.trainer.dto.*;
import com.fithub.fithubbackend.domain.trainer.repository.TrainerCareerRepository;
import com.fithub.fithubbackend.domain.trainer.repository.TrainerLicenseImgRepository;
import com.fithub.fithubbackend.domain.trainer.repository.TrainerRepository;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {
    private final TrainerRepository trainerRepository;
    private final TrainerCareerRepository trainerCareerRepository;
    private final TrainerLicenseImgRepository trainerLicenseImgRepository;

    private final TrainingRepository trainingRepository;

    private final AwsS3Uploader s3Uploader;

    @Override
    public TrainerSpecDto getTrainersSpec(Long userId) {
        Trainer trainer = findTrainerByUserId(userId);
        return TrainerSpecDto.builder()
                .trainerCareerList(trainer.getTrainerCareerList())
                .trainerLicenseList(trainer.getTrainerLicenseImgList())
                .trainerExpertiseList(trainer.getTrainerExpertiseList())
                .address(trainer.getAddress())
                .build();
    }

    @Override
    public TrainerCareerDetailDto getTrainerCareer(Long careerId) {
        TrainerCareer trainerCareer = trainerCareerRepository.findById(careerId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 경력을 찾을 수 없습니다."));
        return TrainerCareerDetailDto.builder().career(trainerCareer).build();
    }

    @Override
    public TrainerLicenseDto getTrainerLicenseImg(Long licenseImgId) {
        TrainerLicenseImg trainerLicenseImg = trainerLicenseImgRepository.findById(licenseImgId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 자격증 이미지를 찾을 수 없습니다."));
        return TrainerLicenseDto.builder().licenseImg(trainerLicenseImg).build();
    }

    @Override
    @Transactional
    public Long createTrainerCareer(Long userId, TrainerCareerRequestDto dto) {
        Trainer trainer = findTrainerByUserId(userId);
        Point point = parsePoint(dto.getLatitude(), dto.getLongitude());

        TrainerCareer trainerCareer = TrainerCareer.careerBuilder()
                .trainer(trainer)
                .dto(dto)
                .point(point)
                .careerBuild();

        if (dto.isWorking()) {
            changeCurrentAddressToThis(trainer, trainerCareer);
        }
        return trainerCareerRepository.save(trainerCareer).getId();
    }

    @Override
    @Transactional
    public Long createTrainerLicenseImg(Long userId, MultipartFile file) {
        Trainer trainer = findTrainerByUserId(userId);
        String path = s3Uploader.imgPath("trainer_license_temp");
        Document document = Document.builder()
                .inputName(file.getOriginalFilename())
                .url(s3Uploader.putS3(file, path))
                .path(path)
                .build();
        TrainerLicenseImg trainerLicenseImg = TrainerLicenseImg.builder().trainer(trainer).document(document).build();
        return trainerLicenseImgRepository.save(trainerLicenseImg).getId();
    }

    @Override
    @Transactional
    public void updateTrainerCareer(Long userId, Long careerId, TrainerCareerRequestDto dto) {
        Trainer trainer = findTrainerByUserId(userId);
        TrainerCareer career = findCareerById(careerId);

        if (!career.getTrainer().getId().equals(trainer.getId())) {
            throw new CustomException(ErrorCode.PERMISSION_DENIED);
        }

        Point point = career.getPoint();
        if (point.getX() != dto.getLongitude() && point.getY() != dto.getLatitude()) {
            point = parsePoint(dto.getLatitude(), dto.getLongitude());
        }
        career.updateCareer(dto, point);

        changeAddressOrQuitCompany(trainer, career, dto.isWorking());
    }

    private void changeAddressOrQuitCompany(Trainer trainer, TrainerCareer trainerCareer, boolean working) {
        if (!trainerCareer.isWorking() && working) {
            changeCurrentAddressToThis(trainer, trainerCareer);
            trainerCareer.joinCompany();
        } else if (trainerCareer.isWorking() && !working) {
            if (trainingRepository.existsByDeletedFalseAndClosedFalseAndTrainerId(trainer.getId())) {
                throw new CustomException(ErrorCode.BAD_REQUEST, "진행중인 트레이닝 모집이 있어 근무지가 필요합니다. 해당 주소로 진행중인 트레이닝 삭제 후 근무지 수정을 진행해야됩니다.");
            }
            trainerCareer.quitCompany();
        }
    }

    private void changeCurrentAddressToThis(Trainer trainer, TrainerCareer trainerCareer) {
        Optional<TrainerCareer> currentWorkingCareer = trainerCareerRepository.findByWorkingTrueAndTrainerId(trainer.getId());
        currentWorkingCareer.ifPresent(TrainerCareer::quitCompany);

        trainer.updateAddress(trainerCareer);
        updateAddressOfOpenTraining(trainer.getId(), trainerCareer);
    }

    private void updateAddressOfOpenTraining(Long trainerId, TrainerCareer trainerCareer) {
        List<Training> trainingList = trainingRepository.findByDeletedFalseAndClosedFalseAndTrainerId(trainerId);
        for (Training training : trainingList) {
            training.updateAddress(trainerCareer);
        }
    }

    @Override
    @Transactional
    public void deleteTrainerCareer(Long userId, Long careerId) {
        Trainer trainer = findTrainerByUserId(userId);
        TrainerCareer career = findCareerById(careerId);

        if (!career.getTrainer().getId().equals(trainer.getId())) {
            throw new CustomException(ErrorCode.PERMISSION_DENIED);
        }

        if (trainer.getAddress().equals(career.getAddress())) {
            if (trainingRepository.existsByDeletedFalseAndClosedFalseAndTrainerId(trainer.getId())) {
                throw new CustomException(ErrorCode.BAD_REQUEST, "진행중인 트레이닝 모집이 있어 근무지가 필요합니다. 해당 주소로 진행중인 트레이닝 삭제 후 근무지 삭제를 진행해야됩니다.");
            }
        }

        trainerCareerRepository.delete(career);
    }

    @Override
    @Transactional
    public void deleteTrainerLicenseImg(Long userId, Long licenseId) {
        Trainer trainer = findTrainerByUserId(userId);
        TrainerLicenseImg licenseImg = findLicenseImgById(licenseId);

        if (!licenseImg.getTrainer().getId().equals(trainer.getId())) {
            throw new CustomException(ErrorCode.PERMISSION_DENIED);
        }

        s3Uploader.deleteS3(licenseImg.getDocument().getPath());
        trainerLicenseImgRepository.delete(licenseImg);
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

    private Trainer findTrainerByUserId (Long userId) {
        return trainerRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.PERMISSION_DENIED, "해당 회원은 트레이너가 아님"));
    }

    private TrainerCareer findCareerById(Long careerId) {
        return trainerCareerRepository.findById(careerId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 경력을 찾을 수 없습니다."));
    }

    private TrainerLicenseImg findLicenseImgById(Long licenseId) {
        return trainerLicenseImgRepository.findById(licenseId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 자격증 이미지를 찾을 수 없습니다."));
    }

}
