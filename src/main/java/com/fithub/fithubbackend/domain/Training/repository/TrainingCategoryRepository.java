package com.fithub.fithubbackend.domain.Training.repository;

import com.fithub.fithubbackend.domain.Training.domain.TrainingCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface TrainingCategoryRepository extends JpaRepository<TrainingCategory, Long> {

    List<TrainingCategory> findByTrainingId(long trainingId);
}
