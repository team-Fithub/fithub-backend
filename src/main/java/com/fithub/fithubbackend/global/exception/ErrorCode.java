package com.fithub.fithubbackend.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 데이터입니다."),
    INVALID_DATE(HttpStatus.CONFLICT, "유효하지 않은 날짜입니다."),
    UNCORRECTABLE_DATA(HttpStatus.CONFLICT, "현재 수정할 수 없는 데이터입니다."),
    DUPLICATE(HttpStatus.CONFLICT,"중복된 데이터 입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
