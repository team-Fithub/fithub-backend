package com.fithub.fithubbackend.global.component;

import com.fithub.fithubbackend.domain.Training.domain.AvailableDate;
import com.fithub.fithubbackend.domain.Training.domain.AvailableTime;
import com.fithub.fithubbackend.domain.Training.domain.Training;
import com.fithub.fithubbackend.domain.Training.repository.AvailableDateRepository;
import com.fithub.fithubbackend.domain.Training.repository.AvailableTimeRepository;
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
    private final AvailableDateRepository dateRepository;
    private final AvailableTimeRepository timeRepository;

    @Async
    @Scheduled(cron = "0 */1 * * *")
    @Transactional
    public void checkTrainingDateTimeValidation() {
        log.info("[SCHEDULE] - checkTrainingDateTimeValidation 실행: {}", LocalDateTime.now());
        List<Training> openTrainingList = trainingRepository.findByClosedFalse();
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        for (Training training : openTrainingList) {
            if (!training.getEndDate().isAfter(date)) {
                // 트레이닝 예약 가능 날짜 중 마지막 날 조회
                AvailableDate lastDate = dateRepository.findFirstByTrainingIdOrderByDateDesc(training.getId()).orElse(null);
                if (lastDate != null) {
                    AvailableTime lastTime = timeRepository.findFirstByAvailableDateIdOrderByTimeDesc(lastDate.getId()).orElse(null);
                    if (lastTime != null && !lastTime.getTime().isAfter(time)) {
                        training.updateClosed(true);
                    } else if (lastTime == null) {
                        training.updateClosed(true);
                    }
                }
            }
        }
    }

}
