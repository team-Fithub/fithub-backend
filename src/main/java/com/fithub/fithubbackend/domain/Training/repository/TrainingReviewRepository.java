package com.fithub.fithubbackend.domain.Training.repository;

import com.fithub.fithubbackend.domain.Training.domain.TrainingReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainingReviewRepository extends JpaRepository<TrainingReview, Long> {
    List<TrainingReview> findByUserIdOrderByIdDesc(Long userId);
    List<TrainingReview> findByLockedFalseAndTrainingId(Long trainingId);

    boolean existsByReserveInfoId(Long reserveInfoId);
    Optional<TrainingReview> findByReserveInfoId(Long reserveInfoId);
}
