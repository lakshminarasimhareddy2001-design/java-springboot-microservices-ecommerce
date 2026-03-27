package com.eswar.paymentservice.dto;

public record PaymentCreateResponse(
        String razorpayOrderId,
        String key,
        String currency,
        Long amount
) {}
