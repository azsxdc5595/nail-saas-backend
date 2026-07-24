package com.nailsaas.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.nailsaas.enums.ErrorCodeEnum;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusinessException(
            BusinessException ex) {

        ErrorCodeEnum errorCode = ex.getErrorCode();

        ApiError error = new ApiError(
                errorCode.getCode(),
                errorCode.getStatus().value(),
                errorCode.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception ex) {

        log.error("Unhandled exception", ex);

        ApiError error = new ApiError(
                "SYSTEM_001",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "系統發生未知錯誤",
                LocalDateTime.now());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}