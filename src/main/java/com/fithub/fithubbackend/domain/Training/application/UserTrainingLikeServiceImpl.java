package com.fithub.fithubbackend.domain.Training.application;

import com.fithub.fithubbackend.domain.Training.domain.Training;
import com.fithub.fithubbackend.domain.Training.domain.TrainingLikes;
import com.fithub.fithubbackend.domain.Training.dto.TrainingLikesInfoDto;
import com.fithub.fithubbackend.domain.Training.dto.TrainingOutlineDto;
import com.fithub.fithubbackend.domain.Training.repository.TrainingLikesRepository;
import com.fithub.fithubbackend.domain.Training.repository.TrainingRepository;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserTrainingLikeServiceImpl implements UserTrainingLikeService {

    private final TrainingRepository trainingRepository;
    private final TrainingLikesRepository trainingLikesRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean isLikesTraining(Long trainingId, User user) {
        if (!trainingRepository.existsById(trainingId)) {
            throw new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 트레이닝입니다.");
        }
        return trainingLikesRepository.existsByTrainingIdAndUserId(trainingId, user.getId());
    }

    @Override
    @Transactional
    public void likesTraining(Long trainingId, User user) {
        Training training = trainingRepository.findById(trainingId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 트레이닝입니다."));
        checkClosed(training.isClosed());

        if (user.getId().equals(training.getTrainer().getUser().getId())) {
            throw new CustomException(ErrorCode.UNKNOWN_ERROR, "트레이너는 자신의 트레이닝을 찜할 수 없습니다.");
        }

        TrainingLikes trainingLikes = TrainingLikes.builder().training(training).user(user).build();
        trainingLikesRepository.save(trainingLikes);
    }

    private void checkClosed(boolean closed) {
        if (closed) {
            throw new CustomException(ErrorCode.UNCORRECTABLE_DATA, "마감된 트레이닝은 찜할 수 없습니다.");
        }
    }

    @Override
    @Transactional
    public void cancelTrainingLikes(Long trainingId, User user) {
        if (!trainingRepository.existsById(trainingId)) {
            throw new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 트레이닝입니다.");
        }
        TrainingLikes trainingLikes = trainingLikesRepository.findByTrainingIdAndUserId(trainingId, user.getId()).orElseThrow(() -> new CustomException(ErrorCode.UNCORRECTABLE_DATA, "트레이닝을 찜하지 않았습니다."));
        trainingLikesRepository.delete(trainingLikes);
    }

    @Override
    public List<TrainingLikesInfoDto> getTrainingLikesList(User user) {
        List<TrainingLikes> trainingLikes = trainingLikesRepository.findByUserId(user.getId());
        return trainingLikes.stream().map(t -> TrainingLikesInfoDto.builder()
                .id(t.getId())
                .trainingOutlineDto(TrainingOutlineDto.toDto(t.getTraining()))
                .build()).toList();
    }
}
