package com.fithub.fithubbackend.domain.trainer.repository;

import com.fithub.fithubbackend.domain.trainer.domain.TrainerCertificationRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainerCertificationRequestRepository extends JpaRepository<TrainerCertificationRequest, Long> {
    boolean existsByRejectedFalseAndUserId(Long userId);
    Page<TrainerCertificationRequest> findAllByRejectedFalse(Pageable pageable);
}
