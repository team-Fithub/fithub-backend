package com.fithub.fithubbackend.domain.Training.application;

import com.fithub.fithubbackend.domain.Training.domain.AvailableDate;
import com.fithub.fithubbackend.domain.Training.domain.AvailableTime;
import com.fithub.fithubbackend.domain.Training.domain.Training;
import com.fithub.fithubbackend.domain.Training.dto.TrainingCreateDto;
import com.fithub.fithubbackend.domain.Training.repository.TrainingRepository;
import com.fithub.fithubbackend.domain.trainer.domain.Trainer;
import com.fithub.fithubbackend.domain.trainer.repository.TrainerRepository;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository trainingRepository;
    private final TrainerRepository trainerRepository;

    @Override
    @Transactional
    public Long createTraining(TrainingCreateDto dto, Long trainerId) {
        Trainer trainer = trainerRepository.findById(trainerId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 트레이너"));

        if (dto.isDateInvalid(dto.getStartDate(), dto.getEndDate())) {
            throw new CustomException(ErrorCode.INVALID_DATE, "선택할 수 없는 날짜입니다.");
        }

        Training training = Training.builder().dto(dto).trainer(trainer).build();

        List<LocalDate> availableDateList = getAvailableDateList(dto.getStartDate(), dto.getEndDate(), dto.getUnableDates());
        List<LocalTime> localTimeList = getAvailableTimeList(dto.getStartHour(), dto.getEndHour());

        for (LocalDate date : availableDateList) {
            AvailableDate availableDate = AvailableDate.builder()
                    .date(date)
                    .enabled(true)
                    .build();
            List<AvailableTime> availableTimeList = localTimeList.stream().map(t -> AvailableTime.builder().date(availableDate).time(t).enabled(true).build()).collect(Collectors.toList());
            availableDate.updateAvailableTimes(availableTimeList);
            availableDate.addTraining(training);
        }
        trainingRepository.save(training);
        return training.getId();
    }

    @Override
    @Transactional
    public Long updateTraining(TrainingCreateDto dto, Long trainingId) {
        // TODO: 접근한 유저(토큰)와 트레이닝에 들어가있는 트레이너가 같은 유저인지 확인하는 과정 필요, SecurityContextHolder에서 꺼내 비교
        Training training = trainingRepository.findById(trainingId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 트레이닝입니다."));
        if (training.isClosed()) {
            throw new CustomException(ErrorCode.UNCORRECTABLE_DATA, "마감된 트레이닝은 수정할 수 없습니다.");
        }
        if (dto.isDateInvalid(dto.getStartDate(), dto.getEndDate())) {
            throw new CustomException(ErrorCode.INVALID_DATE, "선택할 수 없는 날짜입니다.");
        }

        List<AvailableDate> originAvailableDates = new ArrayList<>();
        if (training.getStartDate() != dto.getStartDate() || training.getEndDate() != dto.getEndDate()
                || training.getStartHour() != dto.getStartHour() || training.getEndHour() != dto.getEndHour()) {
            originAvailableDates = training.getAvailableDates();
        }

        // TODO: 기간, 시간 추가 시는 그냥 더해주면 되고 삭제 시에는 예약이 없는지 확인하는 과정 필요. 삭제 선행 작업: 예약
        if (training.getStartDate() != dto.getStartDate() || training.getEndDate() != dto.getEndDate()) {
            List<LocalDate> updateLocalDate = new ArrayList<>();
            // TODO: 예약 추가된 날을 프론트에서 보내줄 수 있는지 or 그냥 기존처럼 안 되는 날만 보내줘서 따로 확인해야되는지
        }


        training.updateTraining(dto);
        return training.getId();
    }

    @Override
    public void deleteTraining(Long id) {
        // TODO: 예약 리스트 확인 후 샥제
    }

    @Override
    @Transactional
    public void updateClosed(Long id, boolean closed) {
        // TODO: 접근한 유저(토큰)와 트레이닝에 들어가있는 트레이너가 같은 유저인지 확인하는 과정 필요, SecurityContextHolder에서 꺼내 비교
        Training training = trainingRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당하는 트레이닝을 찾을 수 없습니다."));
        training.updateClosed(closed);
    }

    private List<LocalDate> getAvailableDateList(LocalDate startLocalDate, LocalDate endLocalDate, List<LocalDate> unableDates) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Date.valueOf(startLocalDate));

        List<LocalDate> dateList = new ArrayList<>();
        Date endDate = Date.valueOf(endLocalDate.plusDays(1));

        while (!calendar.getTime().equals(endDate)) {
            LocalDate date = calendar.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (!unableDates.contains(date)) {
                dateList.add(date);
            }
            calendar.add(Calendar.DATE, 1);
        }

        return dateList;
    }

    private List<LocalTime> getAvailableTimeList(LocalTime startHour, LocalTime endHour) {
        List<LocalTime> timeList = new ArrayList<>();
        while (!startHour.equals(endHour)) {
            timeList.add(startHour);
            startHour = startHour.plusHours(1);
        }
        return timeList;
    }

}
