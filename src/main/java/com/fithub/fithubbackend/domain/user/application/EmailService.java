package com.fithub.fithubbackend.domain.user.application;

import com.fithub.fithubbackend.domain.user.dto.EmailDto;
import com.fithub.fithubbackend.domain.user.dto.EmailNumberDto;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;

public interface EmailService {
    void sendEmail(EmailDto emailDto) throws MessagingException;
    ResponseEntity<String> checkCertificationNumber(EmailNumberDto emailNumberDto);
    void isUserSignedUpAndSendEmail(EmailDto emailDto) throws MessagingException;
}
