package com.eswar.paymentservice.repository;

import com.eswar.paymentservice.entity.PaymentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IPaymentRepository extends JpaRepository<PaymentEntity, UUID> {


        Optional<PaymentEntity> findByOrderId(UUID orderId);

        Optional<PaymentEntity> findByTransactionId(String transactionId);

        Page<PaymentEntity> findByUserId(UUID userId, Pageable pageable);

}
