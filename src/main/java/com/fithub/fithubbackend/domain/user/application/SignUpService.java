package com.fithub.fithubbackend.domain.user.application;

import com.fithub.fithubbackend.domain.user.dto.SignUpDto;
import com.fithub.fithubbackend.domain.user.dto.SignUpResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

public interface SignUpService {
    ResponseEntity<SignUpResponseDto> signUp(@Valid SignUpDto signUpDto, BindingResult bindingResult);
}
