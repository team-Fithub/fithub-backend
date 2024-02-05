package com.fithub.fithubbackend.domain.Training.repository;

import com.fithub.fithubbackend.domain.Training.domain.TrainingDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainingDocumentRepository extends JpaRepository<TrainingDocument, Long> {
    List<TrainingDocument> findByTrainingId(Long trainingId);
}
