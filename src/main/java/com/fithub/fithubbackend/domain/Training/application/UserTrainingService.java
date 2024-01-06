package com.fithub.fithubbackend.domain.Training.application;

import com.fithub.fithubbackend.domain.Training.dto.TrainingInfoDto;
import com.fithub.fithubbackend.domain.Training.dto.TrainingLikesInfoDto;
import com.fithub.fithubbackend.domain.Training.dto.TrainingOutlineDto;
import com.fithub.fithubbackend.domain.Training.dto.UsersReserveInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserTrainingService {
    Page<TrainingOutlineDto> searchAll(Pageable pageable);
    TrainingInfoDto searchById(Long id);

    boolean isLikesTraining(Long trainingId, String email);
    void likesTraining(Long trainingId, boolean likes, String email);
    List<TrainingLikesInfoDto> getTrainingLikesList(String email);

    Page<UsersReserveInfoDto> getTrainingReservationList(String email, Pageable pageable);
}
