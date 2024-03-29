package com.fithub.fithubbackend.domain.trainer.application;

import com.fithub.fithubbackend.domain.Training.domain.Training;
import com.fithub.fithubbackend.domain.Training.domain.TrainingReview;
import com.fithub.fithubbackend.domain.Training.dto.review.UsersTrainingReviewDto;
import com.fithub.fithubbackend.domain.Training.repository.TrainingRepository;
import com.fithub.fithubbackend.domain.Training.repository.TrainingReviewRepository;
import com.fithub.fithubbackend.domain.trainer.domain.Trainer;
import com.fithub.fithubbackend.domain.trainer.dto.TrainerOutlineDto;
import com.fithub.fithubbackend.domain.trainer.dto.TrainerSearchAllReviewDto;
import com.fithub.fithubbackend.domain.trainer.dto.TrainerSearchFilterDto;
import com.fithub.fithubbackend.domain.trainer.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Service
@RequiredArgsConstructor
public class TrainerSearchServiceImpl implements TrainerSearchService {

    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;
    private final TrainingReviewRepository reviewRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<TrainerOutlineDto> searchTrainers(TrainerSearchFilterDto dto, Pageable pageable) {
        Page<Trainer> trainers = trainerRepository.searchTrainers(dto, pageable);
        return trainers.map(TrainerOutlineDto::toDto);
    }

    @Override
    public TrainerSearchAllReviewDto searchTrainerReviews(Long trainerId) {
        List<Training> trainingList = trainingRepository.findByTrainerId(trainerId);
        List<Long> trainingIdList = trainingList.stream().mapToLong(Training::getId).boxed().toList();
        List<UsersTrainingReviewDto> list = new ArrayList<>();

        int reviewNum = 0;
        int sum = 0;
        for (Long id : trainingIdList) {
            List<TrainingReview> trainingReviewList = reviewRepository.findByLockedFalseAndTrainingId(id);
            reviewNum += trainingReviewList.size();
            for (TrainingReview r : trainingReviewList) {
                list.add(UsersTrainingReviewDto.toDto(r));
                sum += r.getStar();
            }
        }

        list.sort(Comparator.comparing(UsersTrainingReviewDto::getCreatedDate, Comparator.reverseOrder()));
        return TrainerSearchAllReviewDto.builder()
                .trainerId(trainerId)
                .reviewNum(reviewNum)
                .average((double)sum / reviewNum)
                .list(list)
                .build();
    }

}
