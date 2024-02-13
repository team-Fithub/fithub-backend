package com.fithub.fithubbackend.domain.Training.repository;

import com.fithub.fithubbackend.domain.Training.domain.Training;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TrainingRepository extends JpaRepository<Training, Long> {

    Page<Training> findAllByDeletedFalseAndClosedFalse(Pageable pageable);
    Page<Training> findAllByDeletedFalseAndTrainerIdAndClosed(Long trainerId, boolean closed, Pageable pageable);

    List<Training> findByClosedFalseAndEndDateLessThanEqual(LocalDate now);
    @Query(value = "SELECT t.title FROM Training t WHERE t.id = :id")
    String findTitleById(@Param("id") Long id);
}
