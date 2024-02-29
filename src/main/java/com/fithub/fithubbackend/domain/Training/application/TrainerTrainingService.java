package com.fithub.fithubbackend.domain.Training.application;

import com.fithub.fithubbackend.domain.Training.dto.TrainersTrainingOutlineDto;
import com.fithub.fithubbackend.domain.Training.dto.reservation.TrainersReserveInfoDto;
import com.fithub.fithubbackend.domain.Training.dto.reservation.TrainingDateReservationNumDto;
import com.fithub.fithubbackend.domain.Training.dto.trainersTraining.TrainingContentUpdateDto;
import com.fithub.fithubbackend.domain.Training.dto.trainersTraining.TrainingCreateDto;
import com.fithub.fithubbackend.domain.Training.dto.trainersTraining.TrainingDateUpdateDto;
import com.fithub.fithubbackend.domain.Training.dto.trainersTraining.TrainingTimeUpdateDto;
import com.fithub.fithubbackend.domain.Training.enums.ReserveStatus;
import com.fithub.fithubbackend.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface TrainerTrainingService {

    Page<TrainersTrainingOutlineDto> getTrainersTrainingList(Long userId, boolean closed, Pageable pageable);

    List<LocalDate> getDateListOfOtherTraining(Long userId);

    Long createTraining(TrainingCreateDto dto, User user);
    Long updateTrainingContent(TrainingContentUpdateDto dto, Long trainingId, String email);

    List<TrainingDateReservationNumDto> getNumberOfReservations(Long trainingId);

    Long updateTrainingDate(String email, Long trainingId, TrainingDateUpdateDto dto);
    Long updateTrainingTime(String email, Long trainingId, TrainingTimeUpdateDto dto);

    void deleteTraining(Long id, String email);

    void closeTraining(Long id, User user);
    void openTraining(Long id, User user);

    Page<TrainersReserveInfoDto> getReservationList(Long userId, ReserveStatus status, Pageable pageable);
    Page<TrainersReserveInfoDto> getReservationListForTrainingId(Long userId, Long trainingId, ReserveStatus status, Pageable pageable);

    void updateReservationStatusNoShow(String email, Long reservationId);
}
