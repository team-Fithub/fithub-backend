package com.fithub.fithubbackend.domain.trainer.repository;

import com.fithub.fithubbackend.domain.trainer.domain.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Long>, CustomTrainerRepository {
    boolean existsByUserId(Long userId);
    Optional<Trainer> findByUserId(Long userId);
}
