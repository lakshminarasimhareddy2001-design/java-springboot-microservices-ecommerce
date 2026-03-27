package com.eswar.paymentservice.exception;

public class PaymentNotFoundException extends BusinessException {
    public PaymentNotFoundException(String message) {
        super("Payment not found","PAYMENT_NOT_FOUND");
    }
}
