package com.fithub.fithubbackend.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponseDto> handleCustomException(CustomException exception) {
        log.info(exception.toString());
        return ErrorResponseDto.toResponseEntity(exception.getErrorCode(), exception.getMessage());
    }
}
