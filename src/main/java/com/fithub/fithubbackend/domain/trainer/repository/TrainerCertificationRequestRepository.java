package com.fithub.fithubbackend.domain.trainer.repository;

import com.fithub.fithubbackend.domain.trainer.domain.TrainerCertificationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainerCertificationRequestRepository extends JpaRepository<TrainerCertificationRequest, Long> {
    boolean existsByUserId(Long userId);
}
