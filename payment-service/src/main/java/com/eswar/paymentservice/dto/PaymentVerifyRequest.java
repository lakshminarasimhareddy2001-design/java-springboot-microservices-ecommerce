package com.eswar.paymentservice.dto;

public record PaymentVerifyRequest(
        String razorpayOrderId,
        String razorpayPaymentId,
        String razorpaySignature
) {}
