package com.eswar.orderservice.service;

import com.eswar.orderservice.constants.OrderStatus;
import com.eswar.orderservice.dto.OrderDto;
import com.eswar.orderservice.dto.OrderItemDto;
import com.eswar.orderservice.dto.OrderResponseDto;
import com.eswar.orderservice.entity.OrderEntity;
import com.eswar.orderservice.entity.OrderedItemEntity;
import com.eswar.orderservice.entity.OrderedItemId;
import com.eswar.orderservice.exceptions.InvalidUserIdException;
import com.eswar.orderservice.exceptions.OrderNotFoundException;
import com.eswar.orderservice.grpc.client.GrpcProductServiceClient;
import com.eswar.orderservice.kafka.event.OrderCreatedEvent;
import com.eswar.orderservice.kafka.event.OrderItemEvent;
import com.eswar.orderservice.kafka.producer.OrderEventProducer;
import com.eswar.orderservice.mapper.IOrderMapper;
import com.eswar.orderservice.repository.IOrderRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderServiceImp implements IOrderService{

    private final IOrderRepository orderRepository;
    private final IOrderMapper mapper;
    private final OrderEventProducer orderEventProducer;
    private final GrpcProductServiceClient grpcProductServiceClient;

    @Override
    @Transactional
    public OrderResponseDto createOrder(OrderDto dto, Principal principal) {

        //getting entity
        OrderEntity order = mapper.toEntity(dto);
        //set status
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
            item.setPrice(price);


            items.add(item);
        }

        order.setItems(items);
        try {
            order.setCustomerId(UUID.fromString(principal.getName()));
        } catch (Exception e) {
            throw new InvalidUserIdException("Invalid userId in token");
        }

        OrderEntity saved = orderRepository.save(order);

        publishOrderCreatedEvent(saved,totalAmount);

        return mapper.toResponse(saved);
    }

    @Override
    public List<OrderResponseDto> getALlOrders(Pageable pageable) {
        return orderRepository.findAll(pageable).stream().map(mapper::toResponse).toList();
    }

    @Override
    public OrderResponseDto getOrderById(String orderId) {
        UUID id;
        try {
            id = UUID.fromString(orderId);
        } catch (Exception e) {
            throw new OrderNotFoundException("INVALID_ORDER_ID ,Order ID is not valid UUID");
        }

        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("ORDER_NOT_FOUND, Order not found"));

        return mapper.toResponse(order);
    }

    @Override
    public OrderResponseDto updateOrder(String orderId, OrderDto orderDto) {
        return null;
    }

    @Override
    public void cancelOrder(String orderId) {

    }

    @Override
    public boolean isOrderOwnedByUser(String orderId, String userId) {
        return false;
    }

    private void publishOrderCreatedEvent(@NonNull OrderEntity order, BigDecimal totalAmount) {

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
