package com.fithub.fithubbackend.global.exception;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.siot.IamportRestClient.exception.IamportResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
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
        FieldError fieldError = exception.getBindingResult().getFieldError();
        String message = fieldError.getField() + ": " + fieldError.getDefaultMessage();
        log.info("[CustomExceptionHandler] - @Valid 에러 {}", message);
        return ErrorResponseDto.toResponseEntity(ErrorCode.INVALID_FORM_DATA, message);
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ErrorResponseDto> handlePropertyReferenceException(PropertyReferenceException exception) {
        log.info("[CustomExceptionHandler] - PropertyReferenceException 에러 {}", exception.getMessage());
        return ErrorResponseDto.toResponseEntity(ErrorCode.INVALID_FORM_DATA, exception.getMessage());
    }

    @ExceptionHandler(IamportResponseException.class)
    public ResponseEntity<ErrorResponseDto> handleIamportResponseException(IamportResponseException exception) {
        log.info("[CustomExceptionHandler] - IamportResponseException 에러 {}", exception.getMessage());
        return ResponseEntity
                .status(exception.getHttpStatusCode())
                .body(ErrorResponseDto.builder()
                        .status(exception.getHttpStatusCode())
                        .code("IAMPORT_ERROR")
                        .message(exception.getMessage())
                        .build()
                );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDto> handleMissingServletRequestParameterException(MissingServletRequestParameterException exception) {
        log.info("[CustomExceptionHandler] - MissingServletRequestParameterException 에러 {}", exception.getMessage());
        return ResponseEntity
                .status(exception.getStatusCode())
                .body(ErrorResponseDto.builder()
                        .status(exception.getStatusCode().value())
                        .code("BAD_REQUEST")
                        .message(exception.getMessage())
                        .build()
                );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        log.info("[CustomExceptionHandler] - HttpMessageNotReadableException 에러 {}", exception.getMessage());
        if (exception.getCause() instanceof MismatchedInputException mismatchedInputException) {
            return ResponseEntity.badRequest()
                    .body(ErrorResponseDto.builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .code("INVALID_JSON")
                            .message(mismatchedInputException.getPath().get(0).getFieldName() + " 필드의 값이 잘못되었습니다.")
                            .build()
                    );
        }
        return ResponseEntity.badRequest()
                .body(ErrorResponseDto.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .code("INVALID_JSON")
                        .message("확인할 수 없는 형태의 데이터가 들어왔습니다")
                        .build()
                );
    }


}

