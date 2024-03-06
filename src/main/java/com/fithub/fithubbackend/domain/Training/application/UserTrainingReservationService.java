package com.fithub.fithubbackend.domain.Training.application;

import com.fithub.fithubbackend.domain.Training.dto.reservation.UsersReserveCompleteOutlineDto;
import com.fithub.fithubbackend.domain.Training.dto.reservation.UsersReserveInfoDto;
import com.fithub.fithubbackend.domain.Training.dto.reservation.UsersReserveOutlineDto;
import com.fithub.fithubbackend.domain.Training.dto.review.TrainingReviewReqDto;
import com.fithub.fithubbackend.domain.Training.dto.review.UsersTrainingReviewDto;
import com.fithub.fithubbackend.domain.Training.enums.ReserveStatus;
import com.fithub.fithubbackend.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserTrainingReservationService {
    Page<UsersReserveOutlineDto> getTrainingReservationList(User user, ReserveStatus status, Pageable pageable);
    Page<UsersReserveCompleteOutlineDto> getTrainingReservationCompleteList(User user, Pageable pageable);

    UsersReserveInfoDto getTrainingReservation(Long reservationId);

    List<UsersTrainingReviewDto> getAllReviews(User user);
    UsersTrainingReviewDto getReviewForReservation(User user, Long reserveId);

    Long writeReviewOnCompletedReservation(User user, TrainingReviewReqDto dto);
    void updateReview(User user, Long reviewId, TrainingReviewReqDto dto);
    void deleteReview(User user, Long reviewId);
}
