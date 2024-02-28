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
    List<Training> findByDeletedFalseAndClosedFalseAndTrainerId(Long trainerId);

    List<Training> findByClosedFalseAndEndDateLessThanEqual(LocalDate now);

    boolean existsByDeletedFalseAndClosedFalseAndTrainerId(Long trainerId);

    @Query(value = "SELECT * FROM Training AS t WHERE t.deleted = false AND t.closed = false AND MBRContains(ST_LINESTRINGFROMTEXT(:pointFormat), t.point)", nativeQuery = true)
    List<Training> findByPoint(@Param("pointFormat")String pointFormat, Pageable pageable);

    @Query(value = "SELECT ST_DISTANCE_SPHERE(POINT(:lon, :lat), t.point) AS dist FROM Training t WHERE t.id = :id", nativeQuery = true)
    Double findDistByPoint(@Param("lon") Double lon, @Param("lat") Double lat, @Param("id") Long id);
}
