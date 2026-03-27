package com.eswar.paymentservice.service;

import com.eswar.paymentservice.constatns.PaymentStatus;
import com.eswar.paymentservice.dto.PageResponse;
import com.eswar.paymentservice.dto.PaymentCreateResponse;
import com.eswar.paymentservice.dto.PaymentResponse;
import com.eswar.paymentservice.dto.PaymentVerifyRequest;
import com.eswar.paymentservice.kafka.events.OrderCreatedEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.UUID;

public interface IPaymentService {
    void processPayment(OrderCreatedEvent event);

    PaymentCreateResponse createPayment(UUID orderId, Principal principal);

    PaymentResponse verifyPayment(PaymentVerifyRequest request, Principal principal);

    PageResponse<PaymentResponse> getMyPayments(Principal principal, Pageable pageable);

    PageResponse<PaymentResponse> getAllPayments(Pageable pageable);

    PaymentResponse getPaymentById(UUID paymentId);

    PaymentResponse updatePaymentStatus(UUID paymentId, PaymentStatus status);

    @Transactional
    PaymentResponse createPayment(UUID orderId);

    @Transactional
    void verifyPayment(String orderId, String paymentId, String signature);
}
