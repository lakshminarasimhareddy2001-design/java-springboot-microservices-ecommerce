package com.eswar.paymentservice.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPaymentSuccess(UUID orderId) {
        kafkaTemplate.send("payment-success", orderId);
    }

    public void sendPaymentFailed(UUID orderId) {
        kafkaTemplate.send("payment-failed", orderId);
    }
}
