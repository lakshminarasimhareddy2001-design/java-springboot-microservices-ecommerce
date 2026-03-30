package com.eswar.paymentservice.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
 // 🔐 Generic
 INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),

 // 💳 Payment errors
 PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Payment not found"),
 PAYMENT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "Access denied to this payment"),
 PAYMENT_ALREADY_PROCESSED(HttpStatus.CONFLICT, "Payment already completed"),
 PAYMENT_ALREADY_VERIFIED(HttpStatus.CONFLICT, "Payment already verified"),

 // 💰 Razorpay / validation
 INVALID_PAYMENT_SIGNATURE(HttpStatus.BAD_REQUEST, "Invalid payment signature"),
 INVALID_WEBHOOK_SIGNATURE(HttpStatus.UNAUTHORIZED, "Invalid webhook signature"),

 // 🔧 External service
 PAYMENT_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "Payment provider unavailable");


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
