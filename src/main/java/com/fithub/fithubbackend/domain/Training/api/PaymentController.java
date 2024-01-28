package com.fithub.fithubbackend.domain.Training.api;

import com.fithub.fithubbackend.domain.Training.application.PaymentService;
import com.fithub.fithubbackend.domain.Training.dto.reservation.CancelReqDto;
import com.fithub.fithubbackend.domain.Training.dto.reservation.PaymentReqDto;
import com.fithub.fithubbackend.domain.Training.dto.reservation.PaymentResDto;
import com.fithub.fithubbackend.domain.Training.dto.reservation.ReserveReqDto;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.global.domain.AuthUser;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import com.siot.IamportRestClient.exception.IamportResponseException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/validation")
    public ResponseEntity<PaymentResDto> validate(@RequestBody @Valid PaymentReqDto dto) throws IamportResponseException, IOException {
        return ResponseEntity.ok(paymentService.validate(dto));
    }

    @PostMapping("/order")
    //TODO: 예약 완료 후 찜에 있는 트레이닝이면 삭제할거냐고 물어보는게 좋을수도
    public ResponseEntity<String> reserveComplete(@RequestBody @Valid ReserveReqDto dto, @AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        paymentService.reserveComplete(dto, user);
        return ResponseEntity.ok().body("완료");
    }

    @PostMapping("/cancel")
    public ResponseEntity<String> cancelPayment(@RequestBody @Valid CancelReqDto dto, @AuthUser User user) throws IamportResponseException, IOException {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        paymentService.cancelPayment(dto, user.getEmail());
        return ResponseEntity.ok().body("완료");
    }

}
