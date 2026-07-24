package com.nailsaas.exception;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiError {

    private String code;

    private int status;

    private String message;

    private LocalDateTime timestamp;
}