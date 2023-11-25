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
    FAIL_AUTHENTICATION(HttpStatus.UNAUTHORIZED, "검증되지 않는 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    TOKEN_NULL(HttpStatus.BAD_REQUEST, "NULL인 토큰입니다."),
    TOKEN_NOT_EQUALS(HttpStatus.BAD_REQUEST, "토큰이 일치하지 않습니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "검증되지 않는 토큰입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
