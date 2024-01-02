package com.fithub.fithubbackend.domain.Training.application;

import com.fithub.fithubbackend.domain.Training.domain.ReserveInfo;
import com.fithub.fithubbackend.domain.Training.domain.Training;
import com.fithub.fithubbackend.domain.Training.dto.PaymentReqDto;
import com.fithub.fithubbackend.domain.Training.dto.PaymentResDto;
import com.fithub.fithubbackend.domain.Training.dto.ReserveReqDto;
import com.fithub.fithubbackend.domain.Training.repository.ReserveInfoRepository;
import com.fithub.fithubbackend.domain.Training.repository.TrainingRepository;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.domain.user.repository.UserRepository;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.Payment;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private IamportClient iamportClient;

    private final TrainingRepository trainingRepository;
    private final UserRepository userRepository;
    private final ReserveInfoRepository reserveInfoRepository;

    @Value("${imp.api.key}")
    private String apiKey;

    @Value("${imp.api.secretkey}")
    private String secretKey;

    @PostConstruct
    public void init() {
        this.iamportClient = new IamportClient(apiKey, secretKey);
    }


    @Override
    @Transactional(readOnly = true)
    public PaymentResDto validate(PaymentReqDto dto) throws IamportResponseException, IOException {
        Training training = trainingRepository.findById(dto.getTrainingId()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 트레이닝입니다."));

        Payment response = iamportClient.paymentByImpUid(dto.getImpUid()).getResponse();
        int paidAmount = response.getAmount().intValue();

        // 결제한 금액과 트레이닝 가격이 다르다면 결제 취소
        if (training.getPrice() != paidAmount) {
            log.error("결제 금액 상이: 결제 금액 - {}, 트레이닝 금액 - {}", paidAmount, training.getPrice());
            iamportClient.cancelPaymentByImpUid(createCancelData(response));
            throw new CustomException(ErrorCode.IAMPORT_PRICE_ERROR);
        }

        log.info("결제 내역 - 구매 번호: {}", response.getMerchantUid());
        return PaymentResDto.builder()
                .impUid(response.getImpUid())
                .merchantUid(response.getMerchantUid())
                .payMethod(response.getPayMethod())
                .amount(response.getAmount().intValue())
                .build();
    }

    @Override
    @Transactional
    public void reserveComplete(ReserveReqDto dto, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 회원입니다."));
        Training training = trainingRepository.findById(dto.getTrainingId()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 트레이닝입니다."));

        ReserveInfo reserveInfo = ReserveInfo.builder().dto(dto).user(user).training(training).build();

        if (!training.removeAvailableDateTime(dto.getReserveDateTime())) {
            log.error("예약 완료 실패 - 예약 가능한 시간대가 없음: {}", dto.getReserveDateTime());
            throw new CustomException(ErrorCode.RESERVE_DATE_OR_TIME_ERROR);
        }

        reserveInfoRepository.save(reserveInfo);
    }

    private CancelData createCancelData(Payment response) {
        log.info("결제 취소 - 포트원 고유 번호: {}, 구매 번호: {}", response.getImpUid(), response.getMerchantUid());
        return new CancelData(response.getImpUid(), true);
    }
}
