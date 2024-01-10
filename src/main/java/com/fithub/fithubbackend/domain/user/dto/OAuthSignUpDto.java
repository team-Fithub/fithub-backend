package com.fithub.fithubbackend.domain.user.dto;

import com.fithub.fithubbackend.domain.user.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import static com.fithub.fithubbackend.domain.user.dto.constants.SignUpDtoConstants.*;

@Getter
@Setter
public class OAuthSignUpDto {

    @NotNull
    @Pattern(regexp = EMAIL_REGEXP, message = "이메일 형식에 맞지 않습니다.")
    private String email;

    @NotNull
    @Pattern(regexp = NAME_REGEXP, message = "특수문자 및 숫자는 포함될 수 없습니다.")
    private String name;

    @NotNull
    @Pattern(regexp = PHONE_NUMBER_REGEXP, message = "전화번호 형식에 맞지 않습니다.")
    @Schema(example = "01012345678")
    private String phone;

    @Schema(example = "introduceMyself")
    private String bio;

    @NotNull
    private Gender gender;

    @NotNull
    @Schema(description = "소셜 회원가입, 로그인시에 저장된 제공자 + id", example = "kakao_121211")
    private String providerId;
}
