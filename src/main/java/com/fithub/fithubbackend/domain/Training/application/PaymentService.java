package com.fithub.fithubbackend.domain.Training.application;

import com.fithub.fithubbackend.domain.Training.dto.reservation.CancelReqDto;
import com.fithub.fithubbackend.domain.Training.dto.reservation.PaymentReqDto;
import com.fithub.fithubbackend.domain.Training.dto.reservation.ReserveReqDto;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.siot.IamportRestClient.exception.IamportResponseException;

import java.io.IOException;

public interface PaymentService {
    Long saveOrder(ReserveReqDto dto, User user);
    void updateAvailableDate(Long availableDateId);
    void updateTrainingStatus(Long trainingId);

    Long validate(PaymentReqDto dto) throws IamportResponseException, IOException;

    void cancelPayment(Long userId, CancelReqDto dto) throws IamportResponseException, IOException;
}
