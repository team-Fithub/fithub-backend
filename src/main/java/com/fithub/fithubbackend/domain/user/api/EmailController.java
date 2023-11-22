package com.fithub.fithubbackend.domain.user.api;

import com.fithub.fithubbackend.domain.user.application.EmailServiceImpl;
import com.fithub.fithubbackend.domain.user.dto.EmailDto;
import com.fithub.fithubbackend.domain.user.dto.EmailNumberDto;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {
    private final EmailServiceImpl emailService;

    @PostMapping("/send")
    public void sendEmail(@RequestBody EmailDto emailDto) throws MessagingException {
        emailService.sendEmail(emailDto);
    }

    @PostMapping("/check")
    public ResponseEntity<String> checkCertificationNumber(@RequestBody EmailNumberDto emailNumberDto){
        return emailService.checkCertificationNumber(emailNumberDto);
    }
}
