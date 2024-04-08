package com.fithub.fithubbackend.domain.Training.application;

import com.fithub.fithubbackend.domain.Training.domain.AvailableDate;
import com.fithub.fithubbackend.domain.Training.domain.AvailableTime;
import com.fithub.fithubbackend.domain.Training.domain.ReserveInfo;
import com.fithub.fithubbackend.domain.Training.domain.Training;
import com.fithub.fithubbackend.domain.Training.dto.reservation.CancelReqDto;
import com.fithub.fithubbackend.domain.Training.dto.reservation.PaymentReqDto;
import com.fithub.fithubbackend.domain.Training.dto.reservation.ReserveReqDto;
import com.fithub.fithubbackend.domain.Training.enums.ReserveStatus;
import com.fithub.fithubbackend.domain.Training.repository.AvailableDateRepository;
import com.fithub.fithubbackend.domain.Training.repository.AvailableTimeRepository;
import com.fithub.fithubbackend.domain.Training.repository.ReserveInfoRepository;
import com.fithub.fithubbackend.domain.Training.repository.TrainingRepository;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import com.fithub.fithubbackend.global.notify.NotificationType;
import com.fithub.fithubbackend.global.notify.dto.NotifyRequestDto;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.Payment;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private IamportClient iamportClient;

    private final TrainingRepository trainingRepository;
    private final AvailableDateRepository availableDateRepository;
    private final AvailableTimeRepository availableTimeRepository;

    private final ReserveInfoRepository reserveInfoRepository;

    private final ApplicationEventPublisher eventPublisher;

    @Value("${imp.api.key}")
    private String apiKey;

    @Value("${imp.api.secretkey}")
    private String secretKey;

    @PostConstruct
    public void init() {
        this.iamportClient = new IamportClient(apiKey, secretKey);
    }

    @Override
    @Transactional
    public Long saveOrder(ReserveReqDto dto, User user) {
        Training training = trainingRepository.findById(dto.getTrainingId()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 트레이닝입니다."));
        if (training.isClosed()) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "마감된 트레이닝은 예약할 수 없습니다.");
        }

        AvailableDate availableDate = availableDateRepository.findById(dto.getReservationDateId()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 예약 날짜는 존재하지 않습니다."));
        if (!availableDate.isEnabled()) {
            throw new CustomException(ErrorCode.DATE_OR_TIME_ERROR, "해당 날짜에 예약 가능한 시간대가 없습니다.");
        }

        AvailableTime availableTime = availableTimeRepository.findById(dto.getReservationTimeId()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 예약 시간은 존재하지 않습니다."));
        if (!availableTime.isEnabled()) {
            throw new CustomException(ErrorCode.DATE_OR_TIME_ERROR, "해당 시간은 이미 예약되었습니다.");
        }

        if (!availableTime.getAvailableDate().equals(availableDate)) {
            throw new CustomException(ErrorCode.DATE_OR_TIME_ERROR, "예약 하려는 날짜에 해당하는 시간이 아닙니다.");
        }

        closeReservationDateTime(availableTime, availableDate);
        availableDateRepository.saveAndFlush(availableDate);
        availableTimeRepository.saveAndFlush(availableTime);

        updateTrainingStatus(training, availableDate.getId());

        ReserveInfo reserveInfo = reserveInfoRepository.save(createReserveInfo(user, training, availableDate, availableTime));

        eventPublisher.publishEvent(createReservationNotifyRequest(training));
        return reserveInfo.getId();
    }

    private void closeReservationDateTime(AvailableTime availableTime, AvailableDate availableDate) {
        availableTime.closeTime();
        if (!availableTimeRepository.existsByEnabledTrueAndAvailableDateIdAndIdNot(availableDate.getId(), availableTime.getId())) {
            availableDate.closeDate();
        }
    }

    private void updateTrainingStatus(Training training, Long availableDateId) {
        if (!availableDateRepository.existsByEnabledTrueAndTrainingIdAndIdNot(training.getId(), availableDateId)) {
            training.updateClosed(true);
        }
    }

    private ReserveInfo createReserveInfo(User user, Training training, AvailableDate availableDate, AvailableTime availableTime) {
        return ReserveInfo.builder()
                .user(user)
                .training(training)
                .date(availableDate)
                .time(availableTime)
                .build();
    }

    private NotifyRequestDto createReservationNotifyRequest(Training training) {
        return NotifyRequestDto.builder()
                .receiver(training.getTrainer().getUser())
                .content("새로운 예약이 생겼습니다.")
                .urlId(null)
                .type(NotificationType.NEW_RESERVATION)
                .build();
    }

    @Override
    @Transactional(noRollbackFor = {CustomException.class})
    public Long validate(PaymentReqDto dto) throws IamportResponseException, IOException {
        ReserveInfo reserveInfo = reserveInfoRepository.findById(dto.getReservationId()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "예약 내역이 없습니다. 다시 진행해주세요."));

        Payment response = iamportClient.paymentByImpUid(dto.getImpUid()).getResponse();
        validateAmount(response, reserveInfo);

        reserveInfo.updatePaymentInfo(dto);

        log.info("결제 내역 - imp_uid: {}, merchant_uid: {}", response.getImpUid(), response.getMerchantUid());
        return reserveInfo.getId();
    }

    private void validateAmount(Payment response, ReserveInfo reserveInfo) throws IamportResponseException, IOException {
        int paidAmount = response.getAmount().intValue();
        if (reserveInfo.getPrice() != paidAmount) {
            log.error("결제 금액 상이: imp_uid- {}, 결제 금액 - {}, 트레이닝 금액 - {}", response.getImpUid(), paidAmount, reserveInfo.getPrice());
            iamportClient.cancelPaymentByImpUid(createCancelData(response));

            openTrainingAndDateTime(reserveInfo.getTraining(), reserveInfo);
            reserveInfoRepository.delete(reserveInfo);
            throw new CustomException(ErrorCode.IAMPORT_PRICE_ERROR);
        }
    }

    @Override
    @Transactional
    public void cancelPayment(Long userId, CancelReqDto dto) throws IamportResponseException, IOException {
        ReserveInfo reserveInfo = cancelComplete(userId, dto);

        Payment response = iamportClient.paymentByImpUid(dto.getImpUid()).getResponse();
        iamportClient.cancelPaymentByImpUid(createCancelData(response));
        eventPublisher.publishEvent(createReservationCancelNotifyRequest(reserveInfo.getTraining()));
    }

    private ReserveInfo cancelComplete(Long userId, CancelReqDto dto) {
        ReserveInfo reserveInfo = reserveInfoRepository.findById(dto.getReservationId()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 결제내역입니다."));
        if (!Objects.equals(reserveInfo.getUser().getId(), userId)) {
            throw new CustomException(ErrorCode.PERMISSION_DENIED, "예약한 회원이 아니므로 예약 취소 권한이 없습니다.");
        }

        if (reserveInfo.getReserveDateTime().toLocalDate().isEqual(LocalDate.now(ZoneId.of("Asia/Seoul")))) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "당일 예약 취소 불가");
        }

        checkReserveStatusIsCancelable(reserveInfo.getStatus());

        reserveInfo.updateStatus(ReserveStatus.CANCEL);
        openTrainingAndDateTime(reserveInfo.getTraining(), reserveInfo);
        return reserveInfo;
    }

    private void checkReserveStatusIsCancelable(ReserveStatus status) {
        if (!status.equals(ReserveStatus.BEFORE)) {
            String message = "진행 전 예약만 취소가 가능합니다.";
            if (status.equals(ReserveStatus.CANCEL)) {
                message = "이미 취소된 예약입니다.";
            }
            throw new CustomException(ErrorCode.INVALID_FORM_DATA, message);
        }
    }

    private void openTrainingAndDateTime(Training training, ReserveInfo reserveInfo) {
        reserveInfo.openDateTime();
        // TODO: 예약이 취소돼서 모집 마감 -> 오픈되었다는 알림
        if (training.isClosed()) {
            training.updateClosed(false);
        }
    }

    private CancelData createCancelData(Payment response) {
        log.info("결제 취소 - 포트원 고유 번호: {}, 구매 번호: {}", response.getImpUid(), response.getMerchantUid());
        return new CancelData(response.getImpUid(), true);
    }

    private NotifyRequestDto createReservationCancelNotifyRequest(Training training) {
        return NotifyRequestDto.builder()
                .receiver(training.getTrainer().getUser())
                .content("트레이닝 예약 취소가 있습니다.")
                .urlId(null)
                .type(NotificationType.CANCEL_RESERVATION)
                .build();
    }
}
