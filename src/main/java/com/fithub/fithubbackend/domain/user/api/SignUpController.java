package com.fithub.fithubbackend.domain.user.api;

import com.fithub.fithubbackend.domain.user.application.SignUpServiceImpl;
import com.fithub.fithubbackend.domain.user.dto.SignUpDto;
import com.fithub.fithubbackend.domain.user.dto.SignUpResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class SignUpController {
    private final SignUpServiceImpl signUpService;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDto> signUp(@RequestBody @Valid SignUpDto signUpDto, BindingResult bindingResult){
        return signUpService.signUp(signUpDto,bindingResult);
    }
}
