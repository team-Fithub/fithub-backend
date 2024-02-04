package com.fithub.fithubbackend.domain.Training.application;

import com.fithub.fithubbackend.domain.Training.dto.reservation.TrainersReserveInfoDto;
import com.fithub.fithubbackend.domain.Training.dto.trainersTraining.TrainingContentUpdateDto;
import com.fithub.fithubbackend.domain.Training.dto.trainersTraining.TrainingCreateDto;
import com.fithub.fithubbackend.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TrainerTrainingService {
    Long createTraining(TrainingCreateDto dto, User user);
    Long updateTrainingContent(TrainingContentUpdateDto dto, Long trainingId, String email);
    void deleteTraining(Long id, String email);

    void closeTraining(Long id, User user);
    void openTraining(Long id, User user);

    Page<TrainersReserveInfoDto> getReservationList(User user, Pageable pageable);

    void updateReservationStatusNoShow(String email, Long reservationId);
}
