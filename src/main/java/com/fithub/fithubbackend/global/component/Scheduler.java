package com.fithub.fithubbackend.global.component;

import com.fithub.fithubbackend.domain.Training.domain.Training;
import com.fithub.fithubbackend.domain.Training.repository.TrainingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Component
@EnableAsync
@RequiredArgsConstructor
public class Scheduler {

    private final TrainingRepository trainingRepository;

    @Async
    @Scheduled(cron = "0 0 */1 * * *")
    @Transactional
    public void checkTrainingDateTimeValidation() {
        log.info("[SCHEDULE] - checkTrainingDateTimeValidation 실행: {}", LocalDateTime.now());
        List<Training> openTrainingList = trainingRepository.findByClosedFalseAndEndDateLessThanEqual(LocalDate.now());
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        for (Training training : openTrainingList) {
            if (training.getEndDate().isEqual(date) && !training.getEndHour().isBefore(time)) continue;
            training.updateClosed(true);
        }
    }

}
