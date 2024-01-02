package com.fithub.fithubbackend.domain.Training.api;

import com.fithub.fithubbackend.domain.Training.application.PaymentService;
import com.fithub.fithubbackend.domain.Training.dto.CancelReqDto;
import com.fithub.fithubbackend.domain.Training.dto.PaymentReqDto;
import com.fithub.fithubbackend.domain.Training.dto.PaymentResDto;
import com.fithub.fithubbackend.domain.Training.dto.ReserveReqDto;
import com.siot.IamportRestClient.exception.IamportResponseException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ResponseEntity<String> reserveComplete(@RequestBody @Valid ReserveReqDto dto, @AuthenticationPrincipal UserDetails userDetails) {
        paymentService.reserveComplete(dto, userDetails.getUsername());
        return ResponseEntity.ok().body("완료");
    }

    @PostMapping("/cancel")
    public ResponseEntity<String> cancelPayment(@RequestBody @Valid CancelReqDto dto, @AuthenticationPrincipal UserDetails userDetails) throws IamportResponseException, IOException {
        paymentService.cancelPayment(dto, userDetails.getUsername());
        return ResponseEntity.ok().body("완료");
    }

}
