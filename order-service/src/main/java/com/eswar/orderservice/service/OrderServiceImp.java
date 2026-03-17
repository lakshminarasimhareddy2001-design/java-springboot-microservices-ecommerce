package com.eswar.orderservice.service;

import com.eswar.orderservice.constants.OrderStatus;
import com.eswar.orderservice.dto.OrderDto;
import com.eswar.orderservice.dto.OrderItemDto;
import com.eswar.orderservice.dto.OrderResponseDto;
import com.eswar.orderservice.entity.OrderEntity;
import com.eswar.orderservice.entity.OrderedItemEntity;
import com.eswar.orderservice.entity.OrderedItemId;
import com.eswar.orderservice.grpc.client.GrpcProductServiceClient;
import com.eswar.orderservice.kafka.event.OrderCreatedEvent;
import com.eswar.orderservice.kafka.event.OrderItemEvent;
import com.eswar.orderservice.kafka.producer.OrderEventProducer;
import com.eswar.orderservice.mapper.IOrderMapper;
import com.eswar.orderservice.repository.IOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderServiceImp implements IOrderService{

    private final IOrderRepository orderRepository;
    private final IOrderMapper mapper;
    private final OrderEventProducer orderEventProducer;
    private final GrpcProductServiceClient grpcProductServiceClient;

    @Override
    public OrderResponseDto createOrder(OrderDto dto) {

        OrderEntity order = mapper.toEntity(dto);

        order.setStatus(OrderStatus.CREATED);

        List<OrderedItemEntity> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItemDto itemDto : dto.items()) {
            // 🔥 CALL gRPC PRODUCT SERVICE
            var product = grpcProductServiceClient.getProduct(itemDto.productId());

            BigDecimal price = BigDecimal.valueOf(product.getPrice());

            // Calculate total
            BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(itemDto.quantity()));
            totalAmount = totalAmount.add(itemTotal);

            OrderedItemId id = new OrderedItemId();
            id.setOrder(order);
            id.setProductId(itemDto.productId());

            OrderedItemEntity item = new OrderedItemEntity();
            item.setId(id);
            item.setQuantity(itemDto.quantity());

            items.add(item);
        }

        order.setItems(items);

        OrderEntity saved = orderRepository.save(order);

        publishOrderCreatedEvent(saved,totalAmount);

        return mapper.toResponse(saved);
    }
    private void publishOrderCreatedEvent(OrderEntity order, BigDecimal totalAmount) {

        List<OrderItemEvent> items = order.getItems().stream()
                .map(i -> new OrderItemEvent(
                        i.getId().getProductId(),
                        i.getQuantity()
                ))
                .toList();

        OrderCreatedEvent event =
                new OrderCreatedEvent(
                        order.getId(),
                        order.getCustomerId(),
                        totalAmount, // ✅ now correct
                        items
                );

        orderEventProducer.sendOrderCreatedEvent(event);
    }
}
