package com.fithub.fithubbackend.domain.Training.repository;

import com.fithub.fithubbackend.domain.Training.domain.AvailableTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvailableTimeRepository extends JpaRepository<AvailableTime, Long> {
    List<AvailableTime> findByAvailableDateIdOrderByTime(Long availableDateId);
    boolean existsByEnabledTrueAndAvailableDateIdAndIdNot(Long availableDateId, Long id);
}
