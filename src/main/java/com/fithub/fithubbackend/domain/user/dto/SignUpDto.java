package com.fithub.fithubbackend.domain.user.dto;

import com.fithub.fithubbackend.domain.user.domain.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SignUpDto {
    private final String PASSWORD_REGEXP = "^[a-zA-Z0-9!@#$%^&*]{8,16}$";
    private final String PHONE_NUMBER_REGEXP = "^\\d{3}-\\d{3,4}-\\d{4}$";
    private final String NAME_REGEXP = "^[a-zA-Z0-9가-힣]$";

    @NotEmpty(message = "아이디 입력은 필수입니다.")
    private String userId;

    @NotEmpty(message = "이름 입력은 필수입니다.")
    @Pattern(regexp = NAME_REGEXP, message = "특수문자는 포함될 수 없습니다.")
    private String name;

    @NotEmpty(message = "닉네임 입력은 필수입니다.")
    private String nickname;

    @NotEmpty(message = "이메일 입력은 필수입니다.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String email;

    @NotEmpty(message = "비밀번호 입력은 필수입니다.")
    @Pattern(regexp = PASSWORD_REGEXP, message = "비밀번호 형식에 맞지 않습니다.")
    private String password;

    @NotEmpty(message = "전화번호 입력은 필수입니다.")
    @Pattern(regexp = PHONE_NUMBER_REGEXP, message = "전화번호 형식에 맞지 않습니다.")
    private String phone;

    @NotNull
    private Gender gender;
}
