package com.fithub.fithubbackend.domain.Training.repository;

import com.fithub.fithubbackend.domain.Training.domain.ReserveInfo;
import com.fithub.fithubbackend.domain.Training.enums.ReserveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReserveInfoRepository extends JpaRepository<ReserveInfo, Long> {
    Page<ReserveInfo> findByTrainerId(Long trainerId, Pageable pageable);
    Page<ReserveInfo> findByUserId(Long userId, Pageable pageable);

    boolean existsByTrainingIdAndStatusNotIn(Long trainingId, ReserveStatus[] statuses);

    List<ReserveInfo> findByReserveDateTimeAndStatus(LocalDateTime now, ReserveStatus status);
}
