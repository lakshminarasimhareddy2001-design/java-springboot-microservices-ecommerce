package com.eswar.gatewayservice.exceptions;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {

    private final com.eswar.gatewayservice.exceptions.ErrorCode errorCode;

    public BusinessException(com.eswar.gatewayservice.exceptions.ErrorCode errorCode) {
        super(errorCode.getMessage()); // default message
        this.errorCode = errorCode;
    }

    public BusinessException(com.eswar.gatewayservice.exceptions.ErrorCode errorCode, String customMessage) {
        super(customMessage); // override message if needed
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode.name();
    }

    public HttpStatus getStatus() {
        return errorCode.getStatus(); // 🔥 dynamic
    }

    public ErrorCode getErrorEnum() {
        return errorCode;
    }
}
