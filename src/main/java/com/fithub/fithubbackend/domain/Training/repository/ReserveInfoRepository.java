package com.fithub.fithubbackend.domain.Training.repository;

import com.fithub.fithubbackend.domain.Training.domain.ReserveInfo;
import com.fithub.fithubbackend.domain.Training.enums.ReserveStatus;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReserveInfoRepository extends JpaRepository<ReserveInfo, Long> {
    Page<ReserveInfo> findByTrainerId(Long trainerId, Pageable pageable);
    boolean existsByTrainingIdAndStatusNotIn(Long training_id, List<@NotNull ReserveStatus> status);

    List<ReserveInfo> findByReserveDateTimeAndStatus(LocalDateTime now, ReserveStatus status);
}
