package com.eswar.paymentservice.test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Profile("dev")
public class FakePaymentGenerator {

    @Value("${razorpay.secret}")
    public  String secret;

    public FakePayment generate() {

        String orderId = "order_SXWLWzly1nkKCQ";
        String paymentId = "pay_" + UUID.randomUUID();

        String signature = generateSignature(orderId, paymentId);

        return new FakePayment(orderId, paymentId, signature);
    }

    private String generateSignature(String orderId, String paymentId) {
        try {
            String payload = orderId + "|" + paymentId;

            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            mac.init(key);

            byte[] raw = mac.doFinal(payload.getBytes());

            StringBuilder hex = new StringBuilder();
            for (byte b : raw) {
                String s = Integer.toHexString(0xff & b);
                if (s.length() == 1) hex.append('0');
                hex.append(s);
            }

            return hex.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Data
    @AllArgsConstructor
    public static class FakePayment {
        private String orderId;
        private String paymentId;
        private String signature;
    }
}