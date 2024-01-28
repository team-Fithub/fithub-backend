package com.fithub.fithubbackend.domain.Training.application;

import com.fithub.fithubbackend.domain.Training.domain.ReserveInfo;
import com.fithub.fithubbackend.domain.Training.domain.Training;
import com.fithub.fithubbackend.domain.Training.domain.TrainingLikes;
import com.fithub.fithubbackend.domain.Training.domain.TrainingReview;
import com.fithub.fithubbackend.domain.Training.dto.*;
import com.fithub.fithubbackend.domain.Training.enums.ReserveStatus;
import com.fithub.fithubbackend.domain.Training.repository.*;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserTrainingServiceImpl implements UserTrainingService {

    private final TrainingRepository trainingRepository;
    private final TrainingLikesRepository trainingLikesRepository;
    private final ReserveInfoRepository reserveInfoRepository;
    private final TrainingReviewRepository trainingReviewRepository;

    private final CustomTrainingRepository customTrainingRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<TrainingOutlineDto> searchAll(Pageable pageable) {
        Page<Training> trainingPage = trainingRepository.findAllByDeletedFalse(pageable);
        return trainingPage.map(TrainingOutlineDto::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public TrainingInfoDto searchById(Long id) {
        Training training = trainingRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당하는 트레이닝이 존재하지 않습니다."));
        if (training.isDeleted()) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "삭제된 트레이닝입니다.");
        }

        TrainingInfoDto dto = TrainingInfoDto.toDto(training);
        if (training.getImages() != null && !training.getImages().isEmpty()) {
            List<TrainingDocumentDto> images = training.getImages().stream().map(TrainingDocumentDto::toDto).toList();
            dto.updateImages(images);
        }
        return dto;
    }

    @Override
    public List<TrainingReviewDto> getTrainingReviews(Long id) {
        List<TrainingReview> trainingReviewList = trainingReviewRepository.findByTrainingId(id);
        return trainingReviewList.stream().map(TrainingReviewDto::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isLikesTraining(Long trainingId, User user) {
        if (!trainingRepository.existsById(trainingId)) {
            throw new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 트레이닝입니다.");
        }
        return trainingLikesRepository.existsByTrainingIdAndUserId(trainingId, user.getId());
    }

    @Override
    @Transactional
    public void likesTraining(Long trainingId, User user) {
        Training training = trainingRepository.findById(trainingId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 트레이닝입니다."));
        checkClosed(training.isClosed());

        if (user.getId().equals(training.getTrainer().getUser().getId())) {
            throw new CustomException(ErrorCode.UNKNOWN_ERROR, "트레이너는 자신의 트레이닝을 찜할 수 없습니다.");
        }

        TrainingLikes trainingLikes = TrainingLikes.builder().training(training).user(user).build();
        trainingLikesRepository.save(trainingLikes);
    }

    private void checkClosed(boolean closed) {
        if (closed) {
            throw new CustomException(ErrorCode.UNCORRECTABLE_DATA, "마감된 트레이닝은 찜할 수 없습니다.");
        }
    }

    @Override
    @Transactional
    public void cancelTrainingLikes(Long trainingId, User user) {
        if (!trainingRepository.existsById(trainingId)) {
            throw new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 트레이닝입니다.");
        }
        TrainingLikes trainingLikes = trainingLikesRepository.findByTrainingIdAndUserId(trainingId, user.getId()).orElseThrow(() -> new CustomException(ErrorCode.UNCORRECTABLE_DATA, "트레이닝을 찜하지 않았습니다."));
        trainingLikesRepository.delete(trainingLikes);
    }

    @Override
    public List<TrainingLikesInfoDto> getTrainingLikesList(User user) {
        List<TrainingLikes> trainingLikes = trainingLikesRepository.findByUserId(user.getId());
        return trainingLikes.stream().map(t -> TrainingLikesInfoDto.builder()
                .id(t.getId())
                .trainingOutlineDto(TrainingOutlineDto.toDto(t.getTraining()))
                .build()).toList();
    }

    @Override
    public Page<TrainingOutlineDto> searchTrainingByConditions(TrainingSearchConditionDto conditions, Pageable pageable) {
        Page<Training> trainingList = customTrainingRepository.searchByConditions(conditions, pageable);
        return trainingList.map(TrainingOutlineDto::toDto);
    }

    @Override
    public Page<UsersReserveInfoDto> getTrainingReservationList(User user, Pageable pageable) {
        Page<ReserveInfo> page = reserveInfoRepository.findByUserId(user.getId(), pageable);
        return page.map(UsersReserveInfoDto::toDto);
    }

    @Override
    public List<UsersTrainingReviewDto> getAllReviews(User user) {
        List<TrainingReview> trainingReviewList = trainingReviewRepository.findByUserIdOrderByIdDesc(user.getId());
        return trainingReviewList.stream().map(UsersTrainingReviewDto::toDto).toList();
    }

    @Override
    public UsersTrainingReviewDto getReviewForReservation(User user, Long reserveId) {
        TrainingReview trainingReview = trainingReviewRepository.findByReserveInfoId(reserveId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 예약에 후기가 존재하지 않습니다."));
        permissionValidate(user.getEmail(), trainingReview.getUser().getEmail());
        return UsersTrainingReviewDto.toDto(trainingReview);
    }

    @Override
    @Transactional
    // TODO: 리뷰 작성 시 트레이닝의 트레이너에게 리뷰가 달렸다는 알림?
    public Long writeReviewOnCompletedReservation(User user, TrainingReviewReqDto dto) {
        ReserveInfo reserveInfo = reserveInfoRepository.findById(dto.getReservationId()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당하는 예약 내역을 찾을 수 없습니다."));

        permissionValidate(user.getEmail(), reserveInfo.getUser().getEmail());
        isReserveInfoStatusComplete(reserveInfo);

        TrainingReview trainingReview = TrainingReview.builder()
                .user(user)
                .reserveInfo(reserveInfo)
                .trainingReviewReqDto(dto)
                .build();

        return trainingReviewRepository.save(trainingReview).getId();
    }

    @Override
    @Transactional
    public void updateReview(User user, Long reviewId, TrainingReviewReqDto dto) {
        ReserveInfo reserveInfo = reserveInfoRepository.findById(dto.getReservationId()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당하는 예약 내역을 찾을 수 없습니다."));
        isReserveInfoStatusComplete(reserveInfo);

        TrainingReview trainingReview = trainingReviewRepository.findById(reviewId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "수정하려는 리뷰가 존재하지 않습니다."));
        permissionValidate(user.getEmail(), trainingReview.getUser().getEmail());

        trainingReview.updateReview(dto.getContent(), dto.getStar());
    }

    @Override
    @Transactional
    public void deleteReview(User user, Long reviewId) {
        TrainingReview trainingReview = trainingReviewRepository.findById(reviewId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당하는 후기가 존재하지 않습니다."));
        permissionValidate(user.getEmail(), trainingReview.getUser().getEmail());
        trainingReviewRepository.delete(trainingReview);
    }

    private void permissionValidate(String authEmail, String email) {
        if (!authEmail.equals(email)) {
            throw new CustomException(ErrorCode.PERMISSION_DENIED, "해당 작업을 수행할 권한이 없습니다.");
        }
    }

    private void isReserveInfoStatusComplete(ReserveInfo reserveInfo) {
        if (!reserveInfo.getStatus().equals(ReserveStatus.COMPLETE)) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "해당 예약은 완료 상태가 아니므로 리뷰 작성이 불가능합니다.");
        }
    }

}
