package com.fithub.fithubbackend.domain.Training.application;

import com.fithub.fithubbackend.domain.Training.domain.*;
import com.fithub.fithubbackend.domain.Training.dto.TrainersTrainingOutlineDto;
import com.fithub.fithubbackend.domain.Training.dto.TrainingAvailableTimeDto;
import com.fithub.fithubbackend.domain.Training.dto.reservation.TrainersReserveInfoDto;
import com.fithub.fithubbackend.domain.Training.dto.reservation.TrainingDateReservationNumDto;
import com.fithub.fithubbackend.domain.Training.dto.trainersTraining.*;
import com.fithub.fithubbackend.domain.Training.enums.ReserveStatus;
import com.fithub.fithubbackend.domain.Training.repository.*;
import com.fithub.fithubbackend.domain.trainer.domain.Trainer;
import com.fithub.fithubbackend.domain.trainer.repository.TrainerRepository;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.global.config.s3.AwsS3Uploader;
import com.fithub.fithubbackend.global.domain.Document;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import com.fithub.fithubbackend.global.util.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TrainerTrainingServiceImpl implements TrainerTrainingService {

    private final TrainingRepository trainingRepository;
    private final TrainingDocumentRepository trainingDocumentRepository;

    private final TrainerRepository trainerRepository;
    private final TrainingLikesRepository trainingLikesRepository;

    private final AvailableDateRepository availableDateRepository;

    private final ReserveInfoRepository reserveInfoRepository;
    private final TrainingReviewRepository trainingReviewRepository;
    private final CustomReserveInfoRepository customReserveInfoRepository;

    private final AwsS3Uploader awsS3Uploader;

    private final String imagePath =  "training";

    @Override
    public Page<TrainersTrainingOutlineDto> getTrainersTrainingList(Long userId, boolean closed, Pageable pageable) {
        Trainer trainer = findTrainerByUserId(userId);
        Page<Training> trainersTrainingList = trainingRepository.findAllByDeletedFalseAndTrainerIdAndClosed(trainer.getId(), closed, pageable);
        return trainersTrainingList.map(t -> TrainersTrainingOutlineDto.builder().training(t).build());
    }

    @Override
    @Transactional
    public Long createTraining(TrainingCreateDto dto, User user) {
        Trainer trainer = findTrainerByUserId(user.getId());
        dateValidate(dto.getStartDate(), dto.getEndDate());

        if (trainer.getPoint() == null) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "현재 트레이너가 근무지가 없어 생성이 불가능합니다.");
        }

        Training training = Training.builder().dto(dto).trainer(trainer).build();

        List<LocalDate> availableDateList = getAvailableDateList(dto.getStartDate(), dto.getEndDate(), dto.getUnableDates());
        List<LocalTime> localTimeList = getAvailableTimeList(dto.getStartHour(), dto.getEndHour());

        saveTrainingDateTime(availableDateList, localTimeList, training);

        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            saveTrainingImages(dto.getImages(), training);
        }

        trainingRepository.save(training);
        return training.getId();
    }

    private void saveTrainingDateTime(List<LocalDate> availableDateList, List<LocalTime> localTimeList, Training training) {
        for (LocalDate date : availableDateList) {
            AvailableDate availableDate = AvailableDate.builder()
                    .date(date)
                    .enabled(true)
                    .build();
            List<AvailableTime> availableTimeList = localTimeList.stream().map(t -> AvailableTime.builder().date(availableDate).time(t).enabled(true).build()).toList();
            availableDate.updateAvailableTimes(availableTimeList);
            availableDate.addTraining(training);
        }
    }

    private void saveTrainingImages(List<MultipartFile> images, Training training) {
        FileUtils.isValidDocument(images);
        for (MultipartFile file : images) {
            String path = awsS3Uploader.imgPath(imagePath);
            Document document = Document.builder()
                    .inputName(file.getOriginalFilename())
                    .url(awsS3Uploader.putS3(file, path))
                    .path(path)
                    .build();
            TrainingDocument trainingDoc = TrainingDocument.builder()
                    .training(training)
                    .document(document)
                    .build();
            training.addImages(trainingDoc);
        }
    }

    @Override
    @Transactional
    public Long updateTrainingContent(TrainingContentUpdateDto dto, Long trainingId, String email) {
        Training training = findTrainingById(trainingId);
        permissionValidate(training.getTrainer(), email);

        training.updateTraining(dto);
        if (dto.getTrainingImgUpdateDto() != null) {
            deleteOrAddImage(dto.getTrainingImgUpdateDto(), training);
        }

        return training.getId();
    }

    @Override
    public List<TrainingDateReservationNumDto> getNumberOfReservations(Long trainingId) {
        Training training = findTrainingById(trainingId);
        List<AvailableDate> availableDates = training.getAvailableDates();

        List<TrainingDateReservationNumDto> list = new ArrayList<>();
        for (AvailableDate date : availableDates) {
            Long reservationNum = reserveInfoRepository.countByAvailableDateIdAndStatus(date.getId(), ReserveStatus.BEFORE);
            list.add(createTrainingDateReservationNumDto(date, reservationNum));
        }
        return list;
    }

    private TrainingDateReservationNumDto createTrainingDateReservationNumDto(AvailableDate date, Long reservationNum) {
        return TrainingDateReservationNumDto.builder()
                .id(date.getId())
                .date(date.getDate())
                .availableTimes(date.getAvailableTimes().stream().map(TrainingAvailableTimeDto::toDto).toList())
                .reservationNum(reservationNum)
                .build();
    }

    @Override
    @Transactional
    public Long updateTrainingDate(String email, Long trainingId, TrainingDateUpdateDto dto) {
        Training training = findTrainingById(trainingId);
        permissionValidate(training.getTrainer(), email);

        List<LocalDate> newAvailableDateList = getAvailableDateList(dto.getStartDate(), dto.getEndDate(), dto.getUnableDates());
        List<LocalTime> localTimeList = getAvailableTimeList(training.getStartHour(), training.getEndHour());

        List<AvailableDate> currentDateList = training.getAvailableDates();

        updateCurrentDates(currentDateList, newAvailableDateList, training);
        saveTrainingDateTime(newAvailableDateList, localTimeList, training);

        training.updateStartAndEndDate(dto.getStartDate(), dto.getEndDate());
        return trainingId;
    }

    public void updateCurrentDates(List<AvailableDate> currentDateList, List<LocalDate> newAvailableDateList, Training training) {
        List<AvailableDate> datesToRemove = new ArrayList<>();

        for (AvailableDate date : currentDateList) {
            if (newAvailableDateList.contains(date.getDate())) {
                newAvailableDateList.remove(date.getDate());
            } else {
                datesToRemove.add(date);
            }
        }

        for (AvailableDate date : datesToRemove) {
            handleDeleteData(date, training);
        }
    }

    public void handleDeleteData(AvailableDate date, Training training) {
        List<ReserveStatus> statusList = reserveInfoRepository.findStatusByAvailableDateId(date.getId());
        for (ReserveStatus status : statusList) {
            if (status == ReserveStatus.BEFORE) {
                throw new CustomException(ErrorCode.BAD_REQUEST, date.getDate() + "일에 진행 전 예약이 존재하여 수정할 수 없습니다.");
            }
        }

        if (statusList.isEmpty()) {
            training.removeDate(date);
            availableDateRepository.delete(date);
        } else {
            date.deleteDate();
        }
    }

    @Override
    public Long updateTrainingTime(String email, Long trainingId, TrainingTimeUpdateDto dto) {
        return null;
    }

    private void deleteOrAddImage(TrainingImgUpdateDto dto, Training training) {
        if (dto.isImgDeleted() && !dto.getUnModifiedImgList().isEmpty()) {
            deleteOriginalImage(dto.getUnModifiedImgList(), training);
        }
        if (dto.isImgAdded() && !dto.getNewImgList().isEmpty()) {
            saveTrainingImages(dto.getNewImgList(), training);
        }
    }

    private void deleteOriginalImage(List<String> unModifiedImgList, Training training) {
        List<TrainingDocument> originalImgList = trainingDocumentRepository.findByTrainingId(training.getId());
        for (TrainingDocument originalImg : originalImgList) {
            if (!unModifiedImgList.contains(originalImg.getDocument().getUrl())) {
                training.removeImage(originalImg);
                awsS3Uploader.deleteS3(originalImg.getDocument().getPath());
            }
        }
    }

    @Override
    @Transactional
    public void deleteTraining(Long id, String email) {
        Training training = findTrainingById(id);
        permissionValidate(training.getTrainer(), email);

        if (reserveInfoRepository.existsByTrainingIdAndStatusNotIn(id,
                List.of(ReserveStatus.CANCEL, ReserveStatus.NOSHOW, ReserveStatus.COMPLETE))) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "해당 트레이닝에 완료 또는 취소되지 않은 예약이 존재해 삭제 작업이 불가능합니다.");
        }

        List<TrainingLikes> trainingLikesList = trainingLikesRepository.findByTrainingId(id);
        trainingLikesRepository.deleteAll(trainingLikesList);

        List<TrainingDocument> trainingImgList = trainingDocumentRepository.findByTrainingId(id);
        for (TrainingDocument trainingImg : trainingImgList) {
            awsS3Uploader.deleteS3(trainingImg.getDocument().getPath());
        }
        trainingDocumentRepository.deleteAll(trainingImgList);

        if (reserveInfoRepository.existsByTrainingId(id)) {
            training.executeDelete();
        } else {
            trainingRepository.delete(training);
        }
    }

    @Override
    @Transactional
    public void closeTraining(Long id, User user) {
        Training training = findTrainingById(id);
        permissionValidate(training.getTrainer(), user.getEmail());
        training.updateClosed(true);
    }

    @Override
    @Transactional
    public void openTraining(Long id, User user) {
        Training training = findTrainingById(id);
        permissionValidate(training.getTrainer(), user.getEmail());
        // 트레이닝 예약 가능한 마지막 날이 현재보다 이전이면 오픈 불가능
        if (!training.getEndDate().isAfter(LocalDate.now())) {
            throw new CustomException(ErrorCode.UNCORRECTABLE_DATA, "트레이닝 마지막 예약 날짜가 현재 날짜 이후가 아니므로 불가능");
        }
        training.updateClosed(false);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TrainersReserveInfoDto> getReservationList(Long userId, ReserveStatus status, Pageable pageable) {
        Trainer trainer = findTrainerByUserId(userId);
        return customReserveInfoRepository.searchTrainersReserveInfo(trainer.getId(), status, pageable);
    }

    @Override
    public Page<TrainersReserveInfoDto> getReservationListForTrainingId(Long userId, Long trainingId, ReserveStatus status, Pageable pageable) {
        Trainer trainer = findTrainerByUserId(userId);
        return customReserveInfoRepository.searchTrainersReserveInfoForTrainingId(trainer.getId(), trainingId, status, pageable);
    }

    @Override
    @Transactional
    // TODO: 노쇼 처리 시, 리뷰 비공개 처리 시 예약 회원에게 알림
    public void updateReservationStatusNoShow(String email, Long reservationId) {
        ReserveInfo reserveInfo = reserveInfoRepository.findById(reservationId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 예약은 존재하지 않습니다."));

        isReserveInfoStatusComplete(reserveInfo);
        permissionValidate(reserveInfo.getTrainer(), email);

        reserveInfo.updateStatus(ReserveStatus.NOSHOW);

        Optional<TrainingReview> optionalTrainingReview = trainingReviewRepository.findByReserveInfoId(reserveInfo.getId());
        optionalTrainingReview.ifPresent(TrainingReview::lock);
    }

    private void isReserveInfoStatusComplete(ReserveInfo reserveInfo) {
        if (!reserveInfo.getStatus().equals(ReserveStatus.COMPLETE)) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "해당 예약은 진행 전 / 진행 중 / 취소 상태이므로 노쇼 처리할 수 없습니다. 완료 처리된 예약만 노쇼 처리가 가능합니다.");
        }
    }

    private Trainer findTrainerByUserId (Long userId) {
        return trainerRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.PERMISSION_DENIED, "해당 회원은 트레이너가 아님"));
    }

    private Training findTrainingById(Long trainingId) {
        return trainingRepository.findById(trainingId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당하는 트레이닝을 찾을 수 없습니다."));
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

    public void dateValidate(LocalDate startDate, LocalDate endDate) {
        LocalDate now = LocalDate.now();
        if (startDate.isBefore(now) || endDate.isBefore(now) || endDate.isBefore(startDate)) {
            throw new CustomException(ErrorCode.DATE_OR_TIME_ERROR, "선택할 수 없는 날짜입니다.");
        }
    }

    public void permissionValidate(Trainer trainer, String email) {
        if (!trainer.getEmail().equals(email)) {
            throw new CustomException(ErrorCode.PERMISSION_DENIED, "해당 트레이닝을 수정할 권한이 없습니다.");
        }
    }
}
