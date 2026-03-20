package com.eswar.orderservice.kafka.consumer;

import com.eswar.orderservice.entity.OrderEntity;
import com.eswar.orderservice.constants.OrderStatus;
import com.eswar.orderservice.kafka.event.StockRejectedEvent;
import com.eswar.orderservice.kafka.event.StockReservedEvent;
import com.eswar.orderservice.repository.IOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryEventConsumer {

    private final IOrderRepository orderRepository;

    @KafkaListener(
            topics = "stock-reserved",
            groupId = "order-group",
            containerFactory = "stockReservedListenerFactory"
    )
    public void handleStockReserved(@NonNull StockReservedEvent sr) {


        Optional<OrderEntity> optionalOrder = orderRepository.findById(sr.orderId());
        if (optionalOrder.isPresent()) {
            OrderEntity order = optionalOrder.get();
            order.setStatus(OrderStatus.STOCK_RESERVED);
            orderRepository.save(order);
        } else {
            log.warn("Order not found for id {}", sr.orderId());
        }


    }

    @KafkaListener(
            topics = "stock-rejected",
            groupId = "order-group",
            containerFactory = "stockRejectedListenerFactory"
    )
    public void handleStockRejected(@NonNull StockRejectedEvent sr) {

        log.warn("rejected reason: {}",sr.reason());
        Optional<OrderEntity> optionalOrder = orderRepository.findById(sr.orderId());
        if (optionalOrder.isPresent()) {
            OrderEntity order = optionalOrder.get();
            order.setStatus(OrderStatus.STOCK_FAILED);
            orderRepository.save(order);
        } else {
            log.warn("Order not found for id {}", sr.orderId());
        }

    }
}