package com.fithub.fithubbackend.domain.user.dto;

import jakarta.validation.constraints.Email;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailDto {
    private String to;

    public String certificationNumberFormat (String message, String uuid){
        return String.format(message,uuid);
    }
}
