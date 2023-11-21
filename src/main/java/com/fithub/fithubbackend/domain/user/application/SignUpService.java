package com.fithub.fithubbackend.domain.user.application;

import com.fithub.fithubbackend.domain.user.dto.SignUpDto;
import com.fithub.fithubbackend.domain.user.dto.SignUpResponseDto;
import org.springframework.http.ResponseEntity;

public interface SignUpService {
    ResponseEntity<SignUpResponseDto> signUp(SignUpDto signUpDto);
}
