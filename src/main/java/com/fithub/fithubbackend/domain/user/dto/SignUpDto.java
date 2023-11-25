package com.fithub.fithubbackend.domain.user.dto;

import com.fithub.fithubbackend.domain.user.domain.Gender;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.fithub.fithubbackend.domain.user.dto.constants.SignUpDtoConstants.NAME_REGEXP;
import static com.fithub.fithubbackend.domain.user.dto.constants.SignUpDtoConstants.EMAIL_REGEXP;
import static com.fithub.fithubbackend.domain.user.dto.constants.SignUpDtoConstants.PASSWORD_REGEXP;
import static com.fithub.fithubbackend.domain.user.dto.constants.SignUpDtoConstants.PHONE_NUMBER_REGEXP;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignUpDto {
    @NotBlank(message = "이메일 입력은 필수입니다.")
    @Pattern(regexp = EMAIL_REGEXP, message = "이메일 형식에 맞지 않습니다.")
    private String email;

    @NotBlank(message = "비밀번호 입력은 필수입니다.")
    @Pattern(regexp = PASSWORD_REGEXP, message = "비밀번호 형식에 맞지 않습니다.(8자 이상 특수문자 포함)")
    private String password;

    @NotBlank(message = "이름 입력은 필수입니다.")
    @Pattern(regexp = NAME_REGEXP, message = "특수문자 및 숫자는 포함될 수 없습니다.")
    private String name;

    @NotBlank(message = "닉네임 입력은 필수입니다.")
    private String nickname;

    @NotBlank(message = "전화번호 입력은 필수입니다.")
    @Pattern(regexp = PHONE_NUMBER_REGEXP, message = "전화번호 형식에 맞지 않습니다.")
    private String phone;

    private String bio;

    @NotNull
    private Gender gender;
}
