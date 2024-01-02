package com.fithub.fithubbackend.domain.Training.repository;

import com.fithub.fithubbackend.domain.Training.domain.ReserveInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReserveInfoRepository extends JpaRepository<ReserveInfo, Long> {
}
