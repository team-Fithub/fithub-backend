package com.fithub.fithubbackend.domain.Training.repository;

import com.fithub.fithubbackend.domain.Training.domain.TrainingLikes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainingLikesRepository extends JpaRepository<TrainingLikes, Long> {
    Optional<TrainingLikes> findByTrainingIdAndUserId(Long trainingId, Long userId);
    boolean existsByTrainingIdAndUserId(Long trainingId, Long userId);
    List<TrainingLikes> findByUserId(Long userId);

    List<TrainingLikes> findByTrainingId(Long trainingId);
}
