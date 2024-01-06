package com.fithub.fithubbackend.domain.Training.application;

import com.fithub.fithubbackend.domain.Training.domain.ReserveInfo;
import com.fithub.fithubbackend.domain.Training.domain.Training;
import com.fithub.fithubbackend.domain.Training.domain.TrainingLikes;
import com.fithub.fithubbackend.domain.Training.dto.*;
import com.fithub.fithubbackend.domain.Training.repository.ReserveInfoRepository;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserTrainingServiceImpl implements UserTrainingService {

    private final TrainingRepository trainingRepository;
    private final TrainingLikesRepository trainingLikesRepository;
    private final UserRepository userRepository;
    private final ReserveInfoRepository reserveInfoRepository;

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
        TrainingInfoDto dto = TrainingInfoDto.toDto(training);
        if (training.getImages() != null && !training.getImages().isEmpty()) {
            List<TrainingDocumentDto> images = training.getImages().stream().map(TrainingDocumentDto::toDto).toList();
            dto.updateImages(images);
        }
        return dto;
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

    @Override
    public List<TrainingLikesInfoDto> getTrainingLikesList(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 회원입니다."));
        List<TrainingLikes> trainingLikes = trainingLikesRepository.findByUserId(user.getId());
        return trainingLikes.stream().map(t -> TrainingLikesInfoDto.builder()
                .id(t.getId())
                .trainingOutlineDto(TrainingOutlineDto.toDto(t.getTraining()))
                .build()).toList();
    }

    @Override
    public Page<UsersReserveInfoDto> getTrainingReservationList(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 회원입니다."));
        Page<ReserveInfo> page = reserveInfoRepository.findByUserId(user.getId(), pageable);

        return page.map(UsersReserveInfoDto::toDto);
    }

    private void checkClosed(boolean closed) {
        if (closed) {
            throw new CustomException(ErrorCode.UNCORRECTABLE_DATA, "마감된 트레이닝은 찜할 수 없습니다.");
        }
    }
}
