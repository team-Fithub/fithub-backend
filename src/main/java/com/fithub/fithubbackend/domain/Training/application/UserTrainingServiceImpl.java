package com.fithub.fithubbackend.domain.Training.application;

import com.fithub.fithubbackend.domain.Training.domain.Training;
import com.fithub.fithubbackend.domain.Training.domain.TrainingLikes;
import com.fithub.fithubbackend.domain.Training.dto.TrainingInfoDto;
import com.fithub.fithubbackend.domain.Training.dto.TrainingOutlineDto;
import com.fithub.fithubbackend.domain.Training.repository.TrainingLikesRepository;
import com.fithub.fithubbackend.domain.Training.repository.TrainingRepository;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.domain.user.repository.UserRepository;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserTrainingServiceImpl implements UserTrainingService {

    private final TrainingRepository trainingRepository;
    private final TrainingLikesRepository trainingLikesRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<TrainingOutlineDto> searchAll(Pageable pageable) {
        Page<Training> trainingPage = trainingRepository.findAll(pageable);
        return trainingPage.map(TrainingOutlineDto::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public TrainingInfoDto searchById(Long id) {
        Training training = trainingRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당하는 트레이닝이 존재하지 않습니다."));
        return TrainingInfoDto.toDto(training);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isLikesTraining(Long trainingId, String email) {
        Training training = trainingRepository.findById(trainingId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 트레이닝입니다."));
        checkClosed(training.isClosed());
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 회원입니다."));
        return trainingLikesRepository.existsByTrainingIdAndUserId(trainingId, user.getId());
    }

    @Override
    @Transactional
    public void likesTraining(Long trainingId, boolean likes, String email) {
        Training training = trainingRepository.findById(trainingId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 트레이닝입니다."));
        checkClosed(training.isClosed());

        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 회원입니다."));
        if (user.getId().equals(training.getTrainer().getUser().getId())) {
            throw new CustomException(ErrorCode.UNKNOWN_ERROR, "트레이너는 자신의 트레이닝을 찜할 수 없습니다.");
        }
        if (likes) {
            TrainingLikes trainingLikes = TrainingLikes.builder().training(training).user(user).build();
            trainingLikesRepository.save(trainingLikes);
        } else {
            TrainingLikes trainingLikes = trainingLikesRepository.findByTrainingIdAndUserId(trainingId, user.getId()).orElseThrow(() -> new CustomException(ErrorCode.UNCORRECTABLE_DATA, "트레이닝을 찜하지 않았습니다."));
            trainingLikesRepository.delete(trainingLikes);
        }
    }

    private void checkClosed(boolean closed) {
        if (closed) {
            throw new CustomException(ErrorCode.UNCORRECTABLE_DATA, "마감된 트레이닝은 찜할 수 없습니다.");
        }
    }
}
