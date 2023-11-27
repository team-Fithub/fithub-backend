package com.fithub.fithubbackend.domain.user.application;

import com.fithub.fithubbackend.domain.user.domain.Document;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.domain.user.dto.SignUpDto;
import com.fithub.fithubbackend.domain.user.dto.SignUpResponseDto;
import com.fithub.fithubbackend.domain.user.dto.constants.SignUpDtoConstants;
import com.fithub.fithubbackend.domain.user.repository.DocumentRepository;
import com.fithub.fithubbackend.domain.user.repository.SignUpRepository;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SignUpServiceImpl implements SignUpService{
    private final SignUpRepository signUpRepository;
    private final PasswordEncoder passwordEncoder;
    private final DocumentRepository documentRepository;

    @Override
    @Transactional
    public ResponseEntity<SignUpResponseDto> signUp(@Valid SignUpDto signUpDto, BindingResult bindingResult){
        formValidate(bindingResult); // 입력 형식 검증
        duplicateEmail(signUpDto.getEmail()); // 이메일 중복 확인
        duplicateNickname(signUpDto.getNickname()); // 닉네임 중복 확인

        Document document = Document.builder()
                .url("test")
                .inputName("test")
                .path("test")
                .build();
        documentRepository.save(document);
        String encodedPassword = passwordEncoder.encode(signUpDto.getPassword()); // 비밀번호 인코딩

        User user = User.builder().signUpDto(signUpDto).encodedPassword(encodedPassword).document(document).build();
        signUpRepository.save(user);
        SignUpResponseDto response = SignUpResponseDto.builder().user(user).build();

        return ResponseEntity.ok(response);
    }

    private void duplicateNickname(String nickname){
        if(signUpRepository.findByNickname(nickname).isPresent())
            throw new CustomException(ErrorCode.DUPLICATE,"중복된 닉네임 입니다.");
    }
    private void duplicateEmail(String email){
        if(signUpRepository.findByEmail(email).isPresent())
            throw new CustomException(ErrorCode.DUPLICATE,"중복된 이메일 입니다.");
    }
    private void formValidate(BindingResult bindingResult){
        String message = String.valueOf(bindingResult.getFieldErrors().stream()
                .findFirst().map(DefaultMessageSourceResolvable::getDefaultMessage))
                .replaceAll(SignUpDtoConstants.FORM_DATA_ERROR_REGEXP,"");

        if (bindingResult.hasErrors()) {
            throw new CustomException(ErrorCode.INVALID_FORM_DATA,message);
        }
    }
}
