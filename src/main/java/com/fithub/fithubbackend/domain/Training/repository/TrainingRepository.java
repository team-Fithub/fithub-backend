package com.fithub.fithubbackend.domain.Training.repository;

import com.fithub.fithubbackend.domain.Training.domain.Training;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TrainingRepository extends JpaRepository<Training, Long> {

    Page<Training> findAllByDeletedFalse(Pageable pageable);
    List<Training> findByClosedFalseAndEndDateLessThanEqual(LocalDate now);
}
