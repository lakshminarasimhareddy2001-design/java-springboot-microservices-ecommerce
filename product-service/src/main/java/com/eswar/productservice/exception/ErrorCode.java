package com.eswar.productservice.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
   //user
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "User already exists"),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "Email already registered"),
    PHONE_ALREADY_EXISTS(HttpStatus.CONFLICT, "Phone number already registered"),

    //auth
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Invalid credentials"),


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
