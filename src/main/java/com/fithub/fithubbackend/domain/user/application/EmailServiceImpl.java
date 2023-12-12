package com.fithub.fithubbackend.domain.user.application;

import com.fithub.fithubbackend.domain.user.dto.EmailDto;
import com.fithub.fithubbackend.domain.user.dto.EmailNumberDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final String SUBJECT = "핏헙 인증메일 입니다.";
    private final String MESSAGE = "인증번호는 %s 입니다.";
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String from;
    private HashMap<String,String> sentMail = new HashMap<>();
    @Override
    public void sendEmail(EmailDto emailDto) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        /* 랜덤한 6자리 영문,숫자 조합 생성*/
        boolean useLetters = true;
        boolean useNumbers = true;
        String certificationNumber = RandomStringUtils.random(6,useLetters,useNumbers);

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
        mimeMessageHelper.setFrom(from);
        mimeMessageHelper.setTo(emailDto.getTo()); // 메일 수신자
        mimeMessageHelper.setSubject(SUBJECT); // 메일 제목
        mimeMessageHelper.setText(emailDto.certificationNumberFormat(MESSAGE,certificationNumber), true); // 메일 본문 내용, HTML 여부
        javaMailSender.send(mimeMessage);
        sentMail.put(emailDto.getTo(),certificationNumber);
    }

    @Override
    public ResponseEntity<String> checkCertificationNumber(EmailNumberDto emailNumberDto) {
        try {
            if (sentMail.get(emailNumberDto.getEmail()).equals(emailNumberDto.getCertificationNumber())) {
                sentMail.remove(emailNumberDto.getEmail());
                return ResponseEntity.ok("Success");
            }
        } catch(NullPointerException e) {
            return new ResponseEntity<>("Fail", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Fail", HttpStatus.BAD_REQUEST);
    }
}
