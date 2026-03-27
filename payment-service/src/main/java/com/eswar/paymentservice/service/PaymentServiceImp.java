package com.eswar.paymentservice.service;

import com.eswar.paymentservice.constatns.PaymentStatus;
import com.eswar.paymentservice.dto.PaymentCreateResponse;
import com.eswar.paymentservice.dto.PaymentResponse;
import com.eswar.paymentservice.entity.PaymentEntity;
import com.eswar.paymentservice.kafka.events.OrderCreatedEvent;
import com.eswar.paymentservice.kafka.producer.PaymentEventProducer;
import com.eswar.paymentservice.repository.IPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImp implements IPaymentService{


    private final IPaymentRepository paymentRepository;
    private final PaymentEventProducer producer;
    private final RazorpayService razorpayService;

    private void validateOwnership(PaymentEntity payment, String userId) {
        if (!payment.getUserId().toString().equals(userId)) {
            throw new RuntimeException("Access Denied");
        }
    }
    @Transactional
    @Override
    public void processPayment(OrderCreatedEvent event) {

        PaymentEntity payment = new PaymentEntity();
        payment.setOrderId(event.orderId());
        payment.setUserId(event.customerId());
        payment.setAmount(event.totalAmount());
        payment.setStatus(PaymentStatus.INITIATED);

        paymentRepository.save(payment);
    }

    @Transactional
    @Override
    public PaymentCreateResponse createPayment(UUID orderId, Principal principal) {

        PaymentEntity payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        String razorpayOrderId = razorpayService.createOrder(payment.getAmount());

        payment.setTransactionId(razorpayOrderId);

        return new PaymentResponse(razorpayOrderId);
    }

    @Transactional
    @Override
    public void verifyPayment(String orderId, String paymentId, String signature) {

        PaymentEntity payment = paymentRepository.findByTransactionId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        boolean isValid = razorpayService.verifySignature(orderId, paymentId, signature);

        if (isValid) {
            payment.setStatus(PaymentStatus.SUCCESS);
            producer.sendPaymentSuccess(payment.getOrderId());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            producer.sendPaymentFailed(payment.getOrderId());
        }
    }
}
