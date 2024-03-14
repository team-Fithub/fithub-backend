package com.fithub.fithubbackend.domain.user.dto;

import com.fithub.fithubbackend.domain.user.enums.Gender;
import com.fithub.fithubbackend.global.common.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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

    @NotEmpty(message = "관심사는 최소 1개 이상 선택해야 합니다. ex) PILATES, HEALTH, PT, CROSSFIT, YOGA")
    @Schema(description = "관심사(최소 1개 이상). ex) PILATES, HEALTH, PT, CROSSFIT, YOGA")
    private List<Category> interests;

}
