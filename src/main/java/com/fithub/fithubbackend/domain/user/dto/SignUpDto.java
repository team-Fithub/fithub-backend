package com.fithub.fithubbackend.domain.user.dto;

import com.fithub.fithubbackend.domain.user.enums.Gender;
import com.fithub.fithubbackend.global.common.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.fithub.fithubbackend.domain.user.dto.constants.SignUpDtoConstants.*;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignUpDto {
    @NotBlank(message = "이메일 입력은 필수입니다.")
    @Pattern(regexp = EMAIL_REGEXP, message = "이메일 형식에 맞지 않습니다.")
    @Schema(example = "fithub@fithub.com")
    private String email;

    @NotBlank(message = "비밀번호 입력은 필수입니다.")
    @Pattern(regexp = PASSWORD_REGEXP, message = "비밀번호 형식에 맞지 않습니다.(8자 이상 특수문자 포함)")
    @Schema(example = "q1w2e3r4!")
    private String password;

    @NotBlank(message = "이름 입력은 필수입니다.")
    @Pattern(regexp = NAME_REGEXP, message = "특수문자 및 숫자는 포함될 수 없습니다.")
    @Schema(example = "name")
    private String name;

    @NotBlank(message = "닉네임 입력은 필수입니다.")
    @Schema(example = "nickname")
    private String nickname;

    @NotBlank(message = "전화번호 입력은 필수입니다.")
    @Pattern(regexp = PHONE_NUMBER_REGEXP, message = "전화번호 형식에 맞지 않습니다.")
    @Schema(example = "01012345678")
    private String phone;

    @Schema(example = "introduceMyself")
    private String bio;

    @NotNull
    private Gender gender;

    @NotEmpty(message = "관심사는 최소 1개 이상 선택해야 합니다. ex) PILATES, HEALTH, PT, CROSSFIT, YOGA")
    @Schema(description = "관심사(최소 1개 이상). ex) PILATES, HEALTH, PT, CROSSFIT, YOGA")
    private List<Category> interests;
}
