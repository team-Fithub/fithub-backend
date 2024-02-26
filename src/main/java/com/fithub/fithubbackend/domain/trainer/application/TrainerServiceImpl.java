package com.fithub.fithubbackend.domain.trainer.application;

import com.fithub.fithubbackend.domain.trainer.domain.Trainer;
import com.fithub.fithubbackend.domain.trainer.domain.TrainerCareer;
import com.fithub.fithubbackend.domain.trainer.domain.TrainerLicenseImg;
import com.fithub.fithubbackend.domain.trainer.dto.TrainerCareerDto;
import com.fithub.fithubbackend.domain.trainer.dto.TrainerCareerRequestDto;
import com.fithub.fithubbackend.domain.trainer.dto.TrainerLicenseDto;
import com.fithub.fithubbackend.domain.trainer.dto.TrainerSpecDto;
import com.fithub.fithubbackend.domain.trainer.repository.TrainerCareerRepository;
import com.fithub.fithubbackend.domain.trainer.repository.TrainerLicenseImgRepository;
import com.fithub.fithubbackend.domain.trainer.repository.TrainerRepository;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {
    private final TrainerRepository trainerRepository;
    private final TrainerCareerRepository trainerCareerRepository;
    private final TrainerLicenseImgRepository trainerLicenseImgRepository;

    @Override
    public TrainerSpecDto getTrainersSpec(Long userId) {
        Trainer trainer = findTrainerByUserId(userId);
        return TrainerSpecDto.builder()
                .trainerCareerList(trainer.getTrainerCareerList())
                .trainerLicenseList(trainer.getTrainerLicenseImgList())
                .build();
    }

    @Override
    public TrainerCareerDto getTrainerCareer(Long careerId) {
        TrainerCareer trainerCareer = trainerCareerRepository.findById(careerId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 경력을 찾을 수 없습니다."));
        return TrainerCareerDto.builder().career(trainerCareer).build();
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

        return trainerCareerRepository.save(trainerCareer).getId();
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

}
