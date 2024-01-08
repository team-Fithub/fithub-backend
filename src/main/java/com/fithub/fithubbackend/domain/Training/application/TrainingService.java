package com.fithub.fithubbackend.domain.Training.application;

import com.fithub.fithubbackend.domain.Training.dto.TrainersReserveInfoDto;
import com.fithub.fithubbackend.domain.Training.dto.TrainingCreateDto;
import com.fithub.fithubbackend.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TrainingService {
    Long createTraining(TrainingCreateDto dto, User user);
    Long updateTraining(TrainingCreateDto dto, Long trainingId, User user);
    void deleteTraining(Long id);

    void closeTraining(Long id, User user);
    void openTraining(Long id, User user);

    Page<TrainersReserveInfoDto> getReservationList(User user, Pageable pageable);
}
