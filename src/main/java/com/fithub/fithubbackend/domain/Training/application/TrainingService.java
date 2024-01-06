package com.fithub.fithubbackend.domain.Training.application;

import com.fithub.fithubbackend.domain.Training.dto.TrainersReserveInfoDto;
import com.fithub.fithubbackend.domain.Training.dto.TrainingCreateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TrainingService {
    Long createTraining(TrainingCreateDto dto, String email);
    Long updateTraining(TrainingCreateDto dto, Long trainingId, String email);
    void deleteTraining(Long id);

    void updateClosed(Long id, boolean closed, String email);

    Page<TrainersReserveInfoDto> getReservationList(String email, Pageable pageable);
}
