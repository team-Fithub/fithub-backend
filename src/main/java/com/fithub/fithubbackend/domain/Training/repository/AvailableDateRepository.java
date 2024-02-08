package com.fithub.fithubbackend.domain.Training.repository;

import com.fithub.fithubbackend.domain.Training.domain.AvailableDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AvailableDateRepository extends JpaRepository<AvailableDate, Long> {
    List<AvailableDate> findByTrainingIdOrderByDate(Long trainingId);
    Optional<AvailableDate> findByTrainingIdAndDate(Long trainingId, LocalDate date);

    boolean existsByEnabledTrueAndTrainingIdAndIdNot(Long trainingId, Long id);
}
