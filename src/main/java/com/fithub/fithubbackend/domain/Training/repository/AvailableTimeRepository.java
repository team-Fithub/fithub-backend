package com.fithub.fithubbackend.domain.Training.repository;

import com.fithub.fithubbackend.domain.Training.domain.AvailableDate;
import com.fithub.fithubbackend.domain.Training.domain.AvailableTime;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;

import java.time.LocalTime;
import java.util.Optional;

public interface AvailableTimeRepository extends JpaRepository<AvailableTime, Long> {

    @NotNull
    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")})
    Optional<AvailableTime> findById(@NotNull Long id);

    boolean existsByEnabledTrueAndAvailableDate(AvailableDate date);
    Optional<AvailableTime> findByEnabledTrueAndAvailableDateIdAndTime(Long availableDateId, LocalTime time);
}
