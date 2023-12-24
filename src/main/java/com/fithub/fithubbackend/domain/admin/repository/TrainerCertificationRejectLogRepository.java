package com.fithub.fithubbackend.domain.admin.repository;

import com.fithub.fithubbackend.domain.admin.domain.TrainerCertificationRejectLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainerCertificationRejectLogRepository extends JpaRepository<TrainerCertificationRejectLog, Long> {
}
