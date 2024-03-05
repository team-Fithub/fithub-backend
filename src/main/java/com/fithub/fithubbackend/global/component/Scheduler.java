package com.fithub.fithubbackend.global.component;

import com.fithub.fithubbackend.domain.Training.domain.AvailableDate;
import com.fithub.fithubbackend.domain.Training.domain.AvailableTime;
import com.fithub.fithubbackend.domain.Training.domain.ReserveInfo;
import com.fithub.fithubbackend.domain.Training.domain.Training;
import com.fithub.fithubbackend.domain.Training.enums.ReserveStatus;
import com.fithub.fithubbackend.domain.Training.repository.AvailableDateRepository;
import com.fithub.fithubbackend.domain.Training.repository.AvailableTimeRepository;
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
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@EnableAsync
@RequiredArgsConstructor
public class Scheduler {

    private final TrainingRepository trainingRepository;
    private final ReserveInfoRepository reserveInfoRepository;
    private final AvailableDateRepository availableDateRepository;
    private final AvailableTimeRepository availableTimeRepository;

    @Async
    @Scheduled(cron = "0 0 */1 * * *")
    @Transactional
    public void checkTrainingDateTimeValidation() {
        LocalDateTime nowDateTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        log.info("[SCHEDULE] - checkTrainingDateTimeValidation 실행: {}", nowDateTime);
        LocalDate date = nowDateTime.toLocalDate();
        LocalTime time = LocalTime.of(nowDateTime.getHour(), 0);

        List<Training> openTrainingList =
                trainingRepository.findByDeletedFalseAndClosedFalseAndStartDateLessThanEqualAndEndDateGreaterThanEqual(date, date);
        for (Training training : openTrainingList) {
            checkTraining(training, date, time);
        }
    }

    private void checkTraining(Training training, LocalDate date, LocalTime time) {
        closeAvailableTimeAndDate(training, date, time);
        closeTraining(training, date, time);
    }

    private void closeAvailableTimeAndDate(Training training, LocalDate date, LocalTime time) {
        Optional<AvailableDate> today = availableDateRepository.findByTrainingIdAndDate(training.getId(), date);
        today.ifPresent(availableDate -> {
            closeTimeIfExists(availableDate.getId(), time);
            closeDateIfTimeAllClosed(availableDate);
        });
    }

    private void closeTimeIfExists(Long dateId, LocalTime time) {
        Optional<AvailableTime> availableTime = availableTimeRepository.findByEnabledTrueAndAvailableDateIdAndTime(dateId, time);
        availableTime.ifPresent(AvailableTime::closeTime);
    }

    private void closeDateIfTimeAllClosed(AvailableDate date) {
        if (!availableTimeRepository.existsByEnabledTrueAndAvailableDateId(date.getId())) {
            date.closeDate();
        }
    }

    private void closeTraining(Training training, LocalDate date, LocalTime time) {
        if (training.getEndDate().isEqual(date) && training.getEndHour().equals(time))
            training.updateClosed(true);
    }


    @Async
    @Scheduled(cron = "0 0 */1 * * *")
    @Transactional
    public void changeReservationStatusToStart() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        log.info("[SCHEDULE] - changeReservationStatusToStart 실행: {}", now);

        LocalDateTime reserveTime = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0);
        changeReservationStatusToStart(reserveTime);

        LocalDateTime timeToChangeComplete = reserveTime.minusHours(1);
        changeReservationStatusToComplete(timeToChangeComplete);
    }

    private void changeReservationStatusToStart(LocalDateTime reserveTime) {
        List<ReserveInfo> reserveInfoListStatusBefore = reserveInfoRepository.findByReserveDateTimeAndStatus(reserveTime, ReserveStatus.BEFORE);
        updateReserveInfoStatus(reserveInfoListStatusBefore, ReserveStatus.START);
    }

    private void changeReservationStatusToComplete(LocalDateTime timeToChangeComplete) {
        List<ReserveInfo> reserveInfoListToChangeComplete = reserveInfoRepository.findByReserveDateTimeAndStatus(timeToChangeComplete, ReserveStatus.START);
        updateReserveInfoStatus(reserveInfoListToChangeComplete, ReserveStatus.COMPLETE);
    }

    private void updateReserveInfoStatus(List<ReserveInfo> reserveInfoList, ReserveStatus newStatus) {
        for (ReserveInfo reserveInfo : reserveInfoList) {
            reserveInfo.updateStatus(newStatus);
        }
    }

}
