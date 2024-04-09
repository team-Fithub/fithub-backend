package com.fithub.fithubbackend.domain.user.repository;

import com.fithub.fithubbackend.domain.user.domain.ClosureReason;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClosureReasonRepository extends JpaRepository<ClosureReason, Long> {
}