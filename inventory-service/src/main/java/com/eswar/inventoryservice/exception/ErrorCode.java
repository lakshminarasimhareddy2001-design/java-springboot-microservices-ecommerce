package com.eswar.inventoryservice.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    //auth
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Token expired"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Invalid credentials"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid token"),
    TOKEN_MALFORMED(HttpStatus.UNAUTHORIZED, "Malformed token"),

    // service (VERY IMPORTANT for microservices)
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "Service temporarily unavailable"),
    DOWNSTREAM_SERVICE_ERROR(HttpStatus.BAD_GATEWAY, "Error from downstream service"),
    TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "Request timeout"),

    // validation
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Invalid request"),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "Validation failed"),

    //genric
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
