package com.fithub.fithubbackend.domain.trainer.repository;

import com.fithub.fithubbackend.domain.trainer.domain.TrainerCareer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainerCareerRepository extends JpaRepository<TrainerCareer, Long> {
    Optional<TrainerCareer> findByWorkingTrueAndTrainerId(Long trainerId);
}
