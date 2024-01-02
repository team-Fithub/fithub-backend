package com.fithub.fithubbackend.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidException(MethodArgumentNotValidException exception) {
        log.info("[CustomExceptionHandler] - @Valid 에러");
        BindingResult bindingResult = exception.getBindingResult();
        String message = bindingResult.getFieldError().getDefaultMessage();
        return ErrorResponseDto.toResponseEntity(ErrorCode.INVALID_FORM_DATA, exception.getMessage());
    }
}
