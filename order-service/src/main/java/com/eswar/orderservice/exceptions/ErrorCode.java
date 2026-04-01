package com.eswar.orderservice.exceptions;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

 // ================= USER =================
 USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
 USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "User already exists"),
 INVALID_USER_ID(HttpStatus.BAD_REQUEST, "Invalid user ID"),
 ACCESS_DENIED(HttpStatus.BAD_REQUEST,"Access is not allowed"),

 // ================= ORDER =================
 ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "Order not found"),
 ORDER_INVALID_ID(HttpStatus.BAD_REQUEST, "Invalid order ID"),
 ORDER_CANNOT_UPDATE(HttpStatus.BAD_REQUEST, "Order cannot be updated"),
 ORDER_ALREADY_CANCELLED(HttpStatus.BAD_REQUEST, "Order already cancelled"),
 ORDER_ACCESS_DENIED(HttpStatus.FORBIDDEN, "You do not have access to this order"),

 // ================= PRODUCT / DOWNSTREAM =================
 PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "Product not found"),
 PRODUCT_SERVICE_FAILED(HttpStatus.BAD_GATEWAY, "Product service unavailable"),

 // ================= AUTH =================
 TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Token expired"),
 INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Invalid credentials"),
 INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid token"),
 TOKEN_MALFORMED(HttpStatus.UNAUTHORIZED, "Malformed token"),

 // ================= SERVICE =================
 SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "Service temporarily unavailable"),
 DOWNSTREAM_SERVICE_ERROR(HttpStatus.BAD_GATEWAY, "Error from downstream service"),
 TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "Request timeout"),

 // ================= VALIDATION =================
 INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Invalid request"),
 VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "Validation failed"),

 // ================= GENERIC =================
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