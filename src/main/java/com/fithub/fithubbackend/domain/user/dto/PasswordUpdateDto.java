package com.fithub.fithubbackend.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "비밀번호 변경 dto")
public class PasswordUpdateDto {

    @NotNull
    private String email;

    @NotNull
    private String password;
}
