package com.nailsaas.exception;

import com.nailsaas.enums.ErrorCodeEnum;

public class BusinessException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

    private final ErrorCodeEnum errorCode;

    public BusinessException(ErrorCodeEnum errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCodeEnum getErrorCode() {
        return errorCode;
    }
}