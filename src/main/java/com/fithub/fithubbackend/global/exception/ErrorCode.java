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
    DUPLICATE(HttpStatus.CONFLICT,"중복된 데이터 입니다."),
    INVALID_FORM_DATA(HttpStatus.CONFLICT,"유효하지 않은 형식의 데이터 입니다."),
    TOKEN_NOT_EQUALS(HttpStatus.BAD_REQUEST, "토큰이 일치하지 않습니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "검증되지 않는 토큰입니다."),
    // TODO JWT 토큰 관련 ERROR CODE 수정 필요
    UNKNOWN_ERROR(HttpStatus.valueOf(400), "토큰이 존재하지 않습니다."),
    WRONG_TYPE_TOKEN(HttpStatus.valueOf(401), "잘못된 타입의 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.valueOf(402), "만료된 토큰입니다."),
    UNSUPPORTED_TOKEN(HttpStatus.valueOf(403), "지원되지 않는 토큰입니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.valueOf(405), "재로그인 필요"),
    INVALID_PWD(HttpStatus.FORBIDDEN, "이메일이나 비밀번호가 틀렸습니다"),

    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),

    AUTHENTICATION_ERROR(HttpStatus.UNAUTHORIZED, "인증에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
