package com.fithub.fithubbackend.domain.user.api;

import com.fithub.fithubbackend.domain.user.application.EmailService;
import com.fithub.fithubbackend.domain.user.dto.EmailDto;
import com.fithub.fithubbackend.domain.user.dto.EmailNumberDto;
import com.fithub.fithubbackend.global.exception.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/email")
public class EmailController {
    private final EmailService emailService;
    @Operation(summary = "이메일 전송(인증번호)", responses = {
            @ApiResponse(responseCode = "200", description = "전송완료"),
    })
    @PostMapping("/send")
    public void sendEmail(@RequestBody EmailDto emailDto) throws MessagingException {
        emailService.sendEmail(emailDto);
    }
    @Operation(summary = "생성된 인증번호와 입력한 인증번호 비교", responses = {
            @ApiResponse(responseCode = "200", description = "생성됨"),
            @ApiResponse(responseCode = "409", description = "생성된 인증번호와 일치하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/check")
    public ResponseEntity<String> checkCertificationNumber(@RequestBody EmailNumberDto emailNumberDto){
        return emailService.checkCertificationNumber(emailNumberDto);
    }

    @Operation(summary = "비밀번호 찾기 위해 회원 가입 여부 확인 및 이메일 전송(인증번호) ", responses = {
            @ApiResponse(responseCode = "200", description = "이메일(인증번호) 전송 완료"),
            @ApiResponse(responseCode = "404", description = "가입되지 않는 이메일", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "소셜 로그인으로 가입된 이메일", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/validation/send")
    public ResponseEntity<Void> isUserSignedUpAndSendEmail(@RequestBody EmailDto emailDto) throws MessagingException {
        emailService.isUserSignedUpAndSendEmail(emailDto);
        return ResponseEntity.ok().build();
    }
}
