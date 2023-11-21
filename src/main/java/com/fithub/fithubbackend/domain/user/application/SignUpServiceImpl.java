package com.fithub.fithubbackend.domain.user.application;

import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.domain.user.dto.SignUpDto;
import com.fithub.fithubbackend.domain.user.dto.SignUpResponseDto;
import com.fithub.fithubbackend.domain.user.repository.SignUpRepository;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpServiceImpl implements SignUpService{
    private final SignUpRepository signUpRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ResponseEntity<SignUpResponseDto> signUp(SignUpDto signUpDto){
        duplicateNickname(signUpDto.getNickname());
        duplicateUserId(signUpDto.getUserId());
        duplicateEmail(signUpDto.getEmail());

        String encodedPassword = passwordEncoder.encode(signUpDto.getPassword());
        User user = User.builder().signUpDto(signUpDto).encodedPassword(encodedPassword).build();

        signUpRepository.save(user);
        SignUpResponseDto response = SignUpResponseDto.builder().user(user).build();

        return ResponseEntity.ok(response);
    }

    private void duplicateNickname(String nickname){
        if(signUpRepository.findByNickName(nickname)){
            throw new CustomException(ErrorCode.DUPLICATE,"중복된 닉네임 입니다.");
        }
    }
    private void duplicateUserId(String userId){
        if(signUpRepository.findByUserId(userId)){
            throw new CustomException(ErrorCode.DUPLICATE,"중복된 아이디 입니다.");
        }
    }
    private void duplicateEmail(String email){
        if(signUpRepository.findByEmail(email)){
            throw new CustomException(ErrorCode.DUPLICATE,"중복된 이메일 입니다.");
        }
    }
}
