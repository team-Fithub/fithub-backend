package com.fithub.fithubbackend.domain.Training.repository;

import com.fithub.fithubbackend.domain.Training.domain.AvailableDate;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AvailableDateRepository extends JpaRepository<AvailableDate, Long> {
    @NotNull
    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")})
    Optional<AvailableDate> findById(@NotNull Long aLong);

    List<AvailableDate> findByTrainingIdOrderByDate(Long trainingId);
    Optional<AvailableDate> findByTrainingIdAndDate(Long trainingId, LocalDate date);

    boolean existsByEnabledTrueAndTrainingIdAndIdNot(Long trainingId, Long id);
}
