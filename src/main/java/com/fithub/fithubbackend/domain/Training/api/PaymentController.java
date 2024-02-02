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
import com.fithub.fithubbackend.global.exception.ErrorResponseDto;
import com.siot.IamportRestClient.exception.IamportResponseException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "payment (결제, 예약 완료)", description = "트레이닝 예약 시 사용하는 결제, 예약 완료 관련 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "결제 전 예약 시간이 예약 가능한지 확인, 저장 api", responses = {
            @ApiResponse(responseCode = "200", description = "예약하려던 시간이 예약 가능해서 예약 정보가 저장됨, 임시로 저장된 예약 정보 id 반환"),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "결제한 트레이닝의 시간대가 예약 불가능해짐", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "예약할 트레이닝이 존재하지 않는 트레이닝", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/order")
    public ResponseEntity<Long> saveOrder(@RequestBody @Valid ReserveReqDto dto, @AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(paymentService.saveOrder(dto, user));
    }

    @Operation(summary = "결제 금액 검증 api", description = "결제가 진행된 후에 결제한 금액과 트레이닝에 등록된 금액이 다른지 검증", responses = {
            @ApiResponse(responseCode = "200", description = "결제 금액과 트레이닝 금액이 같아 검증 성공, 예약 정보에 결제 정보 업데이트"),
            @ApiResponse(responseCode = "404", description = "결제한 트레이닝이 존재하지 않는 트레이닝", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "결제한 금액과 트레이닝 금액이 다름, 결제 취소, 예약 취소됨", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/validation")
    //TODO: 예약 완료 후 찜에 있는 트레이닝이면 삭제할거냐고 물어보거나 바로 삭제?
    public ResponseEntity<PaymentResDto> validate(@RequestBody @Valid PaymentReqDto dto) throws IamportResponseException, IOException {
        return ResponseEntity.ok(paymentService.validate(dto));
    }

    @Operation(summary = "결제,예약 취소(환불)", responses = {
            @ApiResponse(responseCode = "200", description = "취소 성공"),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 예약 내역", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "진행 전 예약이 아니거나 이미 취소된 예약이므로 취소 불가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/cancel")
    public ResponseEntity<String> cancelPayment(@RequestBody @Valid CancelReqDto dto, @AuthUser User user) throws IamportResponseException, IOException {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        paymentService.cancelPayment(dto, user.getEmail());
        return ResponseEntity.ok().body("완료");
    }

}
