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

import java.time.LocalDate;
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
    public void checkTrainingDateTimeValidation() {
        List<Training> openTrainingList = trainingRepository.findByClosedFalse();
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        for (Training training : openTrainingList) {
            if (!training.getEndDate().isAfter(date)) {
                // 트레이닝 예약 가능 날짜 중 마지막 날 조회
                List<AvailableDate> availableDates = dateRepository.findByTrainingIdOrderByDate(training.getId());
                AvailableDate lastDate = availableDates.get(availableDates.size() - 1);
                // 마지막 날의 시간 리스트 조회
                List<AvailableTime> lastDateTimes = timeRepository.findByAvailableDateIdOrderByTime(lastDate.getId());
                if (!lastDateTimes.get(lastDateTimes.size() - 1).getTime().isAfter(time)) {
                    training.updateClosed(true);
                }
            }
        }
    }

}
