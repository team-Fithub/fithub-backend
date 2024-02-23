package com.fithub.fithubbackend.domain.Training.application;

import com.fithub.fithubbackend.domain.Training.domain.ReserveInfo;
import com.fithub.fithubbackend.domain.Training.domain.Training;
import com.fithub.fithubbackend.domain.Training.domain.TrainingReview;
import com.fithub.fithubbackend.domain.Training.dto.reservation.UsersReserveInfoDto;
import com.fithub.fithubbackend.domain.Training.dto.reservation.UsersReserveOutlineDto;
import com.fithub.fithubbackend.domain.Training.dto.review.TrainingReviewReqDto;
import com.fithub.fithubbackend.domain.Training.dto.review.UsersTrainingReviewDto;
import com.fithub.fithubbackend.domain.Training.enums.ReserveStatus;
import com.fithub.fithubbackend.domain.Training.repository.CustomTrainingRepository;
import com.fithub.fithubbackend.domain.Training.repository.ReserveInfoRepository;
import com.fithub.fithubbackend.domain.Training.repository.TrainingRepository;
import com.fithub.fithubbackend.domain.Training.repository.TrainingReviewRepository;
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
public class UserTrainingReservationServiceImpl implements UserTrainingReservationService {

    private final ReserveInfoRepository reserveInfoRepository;
    private final TrainingRepository trainingRepository;
    private final TrainingReviewRepository trainingReviewRepository;
    private final CustomTrainingRepository customTrainingRepository;

    @Override
    public Page<UsersReserveOutlineDto> getTrainingReservationList(User user, ReserveStatus status, Pageable pageable) {
        return customTrainingRepository.searchUsersReserveInfo(user.getId(), status, pageable);
    }

    @Override
    public UsersReserveInfoDto getTrainingReservation(Long reservationId) {
        ReserveInfo reserveInfo = reserveInfoRepository.findById(reservationId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 예약 내역이 존재하지 않습니다"));
        Training training = reserveInfo.getTraining();
        return UsersReserveInfoDto.builder()
                .reservationId(reserveInfo.getId())
                .trainingId(training.getId())
                .title(training.getTitle())
                .address(training.getAddress())
                .reserveDateTime(reserveInfo.getReserveDateTime())
                .price(reserveInfo.getPrice())
                .impUid(reserveInfo.getImpUid())
                .status(reserveInfo.getStatus())
                .paymentDateTime(reserveInfo.getCreatedDate())
                .modifiedDateTime(reserveInfo.getModifiedDate())
                .build();
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
