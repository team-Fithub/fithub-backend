package com.fithub.fithubbackend.domain.Training.application;

import com.fithub.fithubbackend.domain.Training.dto.reservation.CancelReqDto;
import com.fithub.fithubbackend.domain.Training.dto.reservation.PaymentReqDto;
import com.fithub.fithubbackend.domain.Training.dto.reservation.PaymentResDto;
import com.fithub.fithubbackend.domain.Training.dto.reservation.ReserveReqDto;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.siot.IamportRestClient.exception.IamportResponseException;

import java.io.IOException;

public interface PaymentService {
    PaymentResDto validate(PaymentReqDto dto) throws IamportResponseException, IOException;
    void reserveComplete(ReserveReqDto dto, User user);
    void cancelPayment(CancelReqDto dto, String email) throws IamportResponseException, IOException;
}
