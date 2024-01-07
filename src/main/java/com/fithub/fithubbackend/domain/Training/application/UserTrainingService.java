package com.fithub.fithubbackend.domain.Training.application;

import com.fithub.fithubbackend.domain.Training.dto.TrainingInfoDto;
import com.fithub.fithubbackend.domain.Training.dto.TrainingLikesInfoDto;
import com.fithub.fithubbackend.domain.Training.dto.TrainingOutlineDto;
import com.fithub.fithubbackend.domain.Training.dto.UsersReserveInfoDto;
import com.fithub.fithubbackend.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserTrainingService {
    Page<TrainingOutlineDto> searchAll(Pageable pageable);
    TrainingInfoDto searchById(Long id);

    boolean isLikesTraining(Long trainingId, User user);
    void likesTraining(Long trainingId, boolean likes, User user);
    List<TrainingLikesInfoDto> getTrainingLikesList(User user);

    Page<UsersReserveInfoDto> getTrainingReservationList(String email, Pageable pageable);
}
