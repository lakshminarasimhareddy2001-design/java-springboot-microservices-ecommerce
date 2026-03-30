package com.eswar.paymentservice.service;

import com.eswar.paymentservice.constatns.PaymentStatus;
import com.eswar.paymentservice.dto.*;
import com.eswar.paymentservice.entity.PaymentEntity;
import com.eswar.paymentservice.exception.BusinessException;
import com.eswar.paymentservice.exception.ErrorCode;
import com.eswar.paymentservice.kafka.constants.EventStatus;
import com.eswar.paymentservice.kafka.events.OrderCreatedEvent;
import com.eswar.paymentservice.kafka.producer.PaymentEventProducer;
import com.eswar.paymentservice.mapper.IPaymentMapper;
import com.eswar.paymentservice.repository.IPaymentRepository;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImp implements IPaymentService {

    @Value("${razorpay.key}")
    private String razorpayKey;

    @Value("${razorpay.currency:INR}")
    private String currency;

    private final IPaymentRepository paymentRepository;
    private final PaymentEventProducer producer;
    private final RazorpayService razorpayService;
    private final IPaymentMapper mapper;

    // ================= HELPER =================

    private void validateOwnership(PaymentEntity payment, String userId) {
        if (!payment.getUserId().toString().equals(userId)) {
            log.warn("User {} attempted to access payment {} without permission",
                    userId, payment.getId());
            throw new BusinessException(ErrorCode.PAYMENT_ACCESS_DENIED);
        }
    }

    // ================= KAFKA =================

    @Override
    @Transactional
            //for event action when order created
    public void processPayment(OrderCreatedEvent event) {

        PaymentEntity payment = new PaymentEntity();
        payment.setOrderId(event.orderId());
        payment.setUserId(event.customerId());
        payment.setAmount(event.totalAmount());
        payment.setStatus(PaymentStatus.INITIATED);

        paymentRepository.save(payment);
    }

    // ================= USER =================

    @Override
    @Transactional
    //to get order id for frontend
    public PaymentCreateResponse createPayment(UUID orderId, Principal principal) throws RazorpayException {

        if (principal == null) {
            throw new BusinessException(ErrorCode.PAYMENT_ACCESS_DENIED);
        }
        //find payment entity after created by order event
        PaymentEntity payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> {
                    log.error("Payment not found for order {}", orderId);
                    return new BusinessException(ErrorCode.PAYMENT_NOT_FOUND);
                });

        //find owner not allow eg: admin  & others to modify
        validateOwnership(payment, principal.getName());

        // Idempotency check
        if (payment.getTransactionId() != null) {
            return new PaymentCreateResponse(
                    payment.getTransactionId(),
                    razorpayKey,
                    currency,
                    payment.getAmount().multiply(BigDecimal.valueOf(100)).longValue()
            );
        }

        //get order id from razorpay
        String razorpayOrderId;
        try {
             razorpayOrderId = razorpayService.createOrder(payment.getAmount(), currency);
            payment.setTransactionId(razorpayOrderId);
        } catch (RazorpayException ex) {
            log.error("Razorpay service failed for order {}: {}", orderId, ex.getMessage());
            throw new BusinessException(ErrorCode.PAYMENT_SERVICE_UNAVAILABLE);
        }

        //update transaction using razor order id
        payment.setTransactionId(razorpayOrderId);

        return new PaymentCreateResponse(
                razorpayOrderId,
                razorpayKey,
                currency,
                payment.getAmount().multiply(BigDecimal.valueOf(100)).longValue()
        );
    }

    @Override
    @Transactional(readOnly = true)
    //verify payment details from frontend
    public PaymentResponse verifyPayment(PaymentVerifyRequest request, Principal principal) {

        //get payment entity using transaction : order id from razorpay
        PaymentEntity payment = paymentRepository
                .findByTransactionId(request.razorpayOrderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        validateOwnership(payment, principal.getName());


        // Prevent duplicate verification
        if (payment.getStatus() == PaymentStatus.SUCCESS
                || payment.getStatus() == PaymentStatus.PENDING) {

            throw new BusinessException(ErrorCode.PAYMENT_ALREADY_VERIFIED);
        }

        //verify signature
        boolean isValid = razorpayService.verifySignature(
                request.razorpayOrderId(),
                request.razorpayPaymentId(),
                request.razorpaySignature()
        );

        //sending event and update status
        if (isValid) {
            payment.setStatus(PaymentStatus.PENDING);
            payment.setPaymentId(request.razorpayPaymentId());
            return new PaymentResponse("SUCCESS", "Payment verified is pending from webhook");
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            producer.sendPaymentStatus(payment.getOrderId(),EventStatus.FAILED,"Payment Failed or Declined ",null);
            return new PaymentResponse("FAILED", "Invalid payment signature");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PaymentResponse> getMyPayments(Principal principal, Pageable pageable) {

        UUID userId = UUID.fromString(principal.getName());

        var page = paymentRepository.findByUserId(userId, pageable);

        return new PageResponse<>(
                page.getContent().stream().map(mapper::toResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    // ================= ADMIN =================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PaymentResponse> getAllPayments(Pageable pageable) {

        var page = paymentRepository.findAll(pageable);

        return new PageResponse<>(
                page.getContent().stream().map(mapper::toResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(UUID paymentId) {

        PaymentEntity payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->  new BusinessException(ErrorCode.PAYMENT_NOT_FOUND,"Payment is not found with: "+paymentId));

        return mapper.toResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponse updatePaymentStatus(UUID paymentId, PaymentStatus status) {

        PaymentEntity payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND,"Payment is not found with: "+paymentId));

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            throw new BusinessException(ErrorCode.PAYMENT_ALREADY_PROCESSED);
        }

        payment.setStatus(status);

        return mapper.toResponse(payment);
    }


    @Override
    @Transactional
    public void handleWebhook(String payload, String signature) {

        boolean isValid = razorpayService.verifyWebhookSignature(payload, signature);

        if (!isValid) {
            throw new BusinessException(ErrorCode.INVALID_WEBHOOK_SIGNATURE);
        }

        JSONObject event = new JSONObject(payload);
        String eventType = event.getString("event");

        JSONObject paymentJson = event
                .getJSONObject("payload")
                .getJSONObject("payment")
                .getJSONObject("entity");

        String razorpayOrderId = paymentJson.getString("order_id");
        String paymentId = paymentJson.getString("id");

        PaymentEntity payment = paymentRepository
                .findByTransactionId(razorpayOrderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        // ✅ Idempotency
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return;
        }

        if ("payment.captured".equals(eventType)) {

            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaymentId(paymentId);

            producer.sendPaymentStatus(
                    payment.getOrderId(),
                    EventStatus.SUCCESS,
                    "Payment Successful",
                    paymentId
            );

        } else if ("payment.failed".equals(eventType)) {

            payment.setStatus(PaymentStatus.FAILED);

            producer.sendPaymentStatus(
                    payment.getOrderId(),
                    EventStatus.FAILED,
                    "Payment Failed",
                    null
            );
        }

        paymentRepository.save(payment);
    }

}