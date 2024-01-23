package com.fithub.fithubbackend.domain.Training.application;

import com.fithub.fithubbackend.domain.Training.dto.*;
import com.fithub.fithubbackend.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserTrainingService {
    Page<TrainingOutlineDto> searchAll(Pageable pageable);
    TrainingInfoDto searchById(Long id);
    List<TrainingReviewDto> getTrainingReviews(Long id);

    boolean isLikesTraining(Long trainingId, User user);
    void likesTraining(Long trainingId, User user);
    void cancelTrainingLikes(Long trainingId, User user);

    List<TrainingLikesInfoDto> getTrainingLikesList(User user);

    Page<UsersReserveInfoDto> getTrainingReservationList(User user, Pageable pageable);

    List<UsersTrainingReviewDto> getAllReviews(User user);
    UsersTrainingReviewDto getReviewForReservation(User user, Long reserveId);

    Long writeReviewOnCompletedReservation(User user, TrainingReviewReqDto dto);
    void updateReview(User user, Long reviewId, TrainingReviewReqDto dto);
    void deleteReview(User user, Long reviewId);
}
