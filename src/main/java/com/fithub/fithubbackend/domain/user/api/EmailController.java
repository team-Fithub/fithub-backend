package com.fithub.fithubbackend.domain.user.api;

import com.fithub.fithubbackend.domain.user.application.EmailServiceImpl;
import com.fithub.fithubbackend.domain.user.dto.EmailDto;
import com.fithub.fithubbackend.domain.user.dto.EmailNumberDto;
import com.fithub.fithubbackend.global.exception.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class EmailController {
    private final EmailServiceImpl emailService;
    @Operation(summary = "이메일 전송(인증번호)", responses = {
            @ApiResponse(responseCode = "200", description = "전송완료"),
    })
    @PostMapping("/email/send")
    public void sendEmail(@RequestBody EmailDto emailDto) throws MessagingException {
        emailService.sendEmail(emailDto);
    }
    @Operation(summary = "생성된 인증번호와 입력한 인증번호 비교", responses = {
            @ApiResponse(responseCode = "200", description = "생성됨"),
            @ApiResponse(responseCode = "409", description = "생성된 인증번호와 일치하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/email/check")
    public ResponseEntity<String> checkCertificationNumber(@RequestBody EmailNumberDto emailNumberDto){
        return emailService.checkCertificationNumber(emailNumberDto);
    }
}
