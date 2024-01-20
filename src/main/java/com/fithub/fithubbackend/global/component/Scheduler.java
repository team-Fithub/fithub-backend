package com.fithub.fithubbackend.global.component;

import com.fithub.fithubbackend.domain.Training.domain.AvailableDate;
import com.fithub.fithubbackend.domain.Training.domain.ReserveInfo;
import com.fithub.fithubbackend.domain.Training.domain.Training;
import com.fithub.fithubbackend.domain.Training.enums.ReserveStatus;
import com.fithub.fithubbackend.domain.Training.repository.ReserveInfoRepository;
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
    private final ReserveInfoRepository reserveInfoRepository;

    @Async
    @Scheduled(cron = "0 0 */1 * * *")
    @Transactional
    public void checkTrainingDateTimeValidation() {
        log.info("[SCHEDULE] - checkTrainingDateTimeValidation 실행: {}", LocalDateTime.now());
        List<Training> openTrainingList = trainingRepository.findByClosedFalseAndEndDateLessThanEqual(LocalDate.now());
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        for (Training training : openTrainingList) {
            AvailableDate today = training.getToday(date);
            if (today != null && today.isEnabled()) {
                today.closeCurrentTime(time);
                if (today.isAllClosed()) today.closeDate();
            }

            if (training.getEndDate().isEqual(date) && !training.getEndHour().isBefore(time)) continue;
            training.updateClosed(true);
        }
    }

    @Async
    @Scheduled(cron = "0 0 */1 * * *")
    @Transactional
    public void changeReservationStatusToStart() {
        LocalDateTime now = LocalDateTime.now();
        log.info("[SCHEDULE] - changeReservationStatusToStart 실행: {}", now);

        LocalDateTime reserveTime = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0);
        List<ReserveInfo> reserveInfoListStatusBefore = reserveInfoRepository.findByReserveDateTimeAndStatus(reserveTime, ReserveStatus.BEFORE);

        for (ReserveInfo reserveInfo : reserveInfoListStatusBefore) {
            reserveInfo.updateStatus(ReserveStatus.START);
        }

        LocalDateTime timeToChangeComplete = reserveTime.minusHours(1);
        List<ReserveInfo> reserveInfoListToChangeComplete = reserveInfoRepository.findByReserveDateTimeAndStatus(timeToChangeComplete, ReserveStatus.BEFORE);
        for (ReserveInfo reserveInfo : reserveInfoListToChangeComplete) {
            reserveInfo.updateStatus(ReserveStatus.COMPLETE);
        }
    }

}
