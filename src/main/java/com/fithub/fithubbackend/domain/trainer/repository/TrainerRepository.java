package com.fithub.fithubbackend.domain.trainer.repository;

import com.fithub.fithubbackend.domain.trainer.domain.Trainer;
import com.fithub.fithubbackend.domain.trainer.dto.RatingTotalReviewsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Long>, CustomTrainerRepository {
    boolean existsByUserId(Long userId);
    Optional<Trainer> findByUserId(Long userId);

    @Query(value = "SELECT new com.fithub.fithubbackend.domain.trainer.dto.RatingTotalReviewsDto(COALESCE(AVG(tr.star), 0), COUNT(*)) " +
            "FROM TrainingReview tr " +
            "INNER JOIN Training training ON tr.training = training " +
            "INNER JOIN Trainer trainer ON training.trainer = trainer " +
            "INNER JOIN User u ON trainer.user = u " +
            "WHERE trainer.id = :trainerId")
    RatingTotalReviewsDto findRatingAndReviewCountByTrainerId(@Param("trainerId") Long trainerId);

}
