package com.eswar.orderservice.service;

import com.eswar.grpc.user.ProductResponse;
import com.eswar.orderservice.constants.OrderStatus;
import com.eswar.orderservice.dto.OrderDto;
import com.eswar.orderservice.dto.OrderItemDto;
import com.eswar.orderservice.dto.OrderResponseDto;
import com.eswar.orderservice.dto.PageResponse;
import com.eswar.orderservice.entity.OrderEntity;
import com.eswar.orderservice.entity.OrderedItemEntity;
import com.eswar.orderservice.entity.OrderedItemId;
import com.eswar.orderservice.exceptions.BusinessException;
import com.eswar.orderservice.exceptions.ErrorCode;
import com.eswar.orderservice.grpc.client.GrpcProductServiceClient;
import com.eswar.orderservice.grpc.mapper.GrpcExceptionMapper;
import com.eswar.orderservice.kafka.event.OrderCreatedEvent;
import com.eswar.orderservice.kafka.event.OrderItemEvent;
import com.eswar.orderservice.kafka.producer.OrderEventProducer;
import com.eswar.orderservice.mapper.IOrderMapper;
import com.eswar.orderservice.repository.IOrderRepository;
import com.eswar.orderservice.service.IOrderService;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImp implements IOrderService {

    private final IOrderRepository orderRepository;
    private final IOrderMapper mapper;
    private final OrderEventProducer orderEventProducer;
    private final GrpcProductServiceClient grpcProductServiceClient;

    // ================= HELPER =================

    private UUID parseUUID(String id, ErrorCode errorCode) {
        try {
            return UUID.fromString(id);
        } catch (Exception e) {
            throw new BusinessException(errorCode);
        }
    }

    private UUID extractUserId(Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
        return parseUUID(principal.getName(), ErrorCode.INVALID_USER_ID);
    }

    // ================= CREATE ORDER =================

    @Override
    @Transactional
    public OrderResponseDto createOrder(OrderDto dto, Principal principal) {

        UUID customerId = extractUserId(principal);

        OrderEntity order = mapper.toEntity(dto);
        order.setStatus(OrderStatus.CREATED);
        order.setCustomerId(customerId);

        List<OrderedItemEntity> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemDto itemDto : dto.items()) {

            var product = getProductOrThrow(itemDto.productId());

            BigDecimal price = BigDecimal.valueOf(product.getPrice());
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

        OrderEntity saved = orderRepository.save(order);

        publishOrderCreatedEvent(saved, totalAmount);

        return mapper.toResponse(saved);
    }

    // ================= GET =================

    @Override
    public PageResponse<OrderResponseDto> getALlOrders(Pageable pageable) {

        Page<OrderEntity> page = orderRepository.findAll(pageable);

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
    public OrderResponseDto getOrderById(String orderId) {

        UUID id = parseUUID(orderId, ErrorCode.ORDER_INVALID_ID);

        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        return mapper.toResponse(order);
    }

    // ================= UPDATE =================

    @Override
    @Transactional
    public OrderResponseDto updateOrder(String orderId, OrderDto dto) {

        UUID id = parseUUID(orderId, ErrorCode.ORDER_INVALID_ID);

        OrderEntity existing = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        if (existing.getStatus() != OrderStatus.CREATED) {
            throw new BusinessException(ErrorCode.ORDER_CANNOT_UPDATE);
        }

        List<OrderedItemEntity> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemDto itemDto : dto.items()) {

            var product = getProductOrThrow(itemDto.productId());

            BigDecimal price = BigDecimal.valueOf(product.getPrice());
            BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(itemDto.quantity()));

            totalAmount = totalAmount.add(itemTotal);

            OrderedItemId itemId = new OrderedItemId();
            itemId.setOrder(existing);
            itemId.setProductId(itemDto.productId());

            OrderedItemEntity item = new OrderedItemEntity();
            item.setId(itemId);
            item.setQuantity(itemDto.quantity());
            item.setPrice(price);

            items.add(item);
        }

        existing.setItems(items);
        existing.setStatus(OrderStatus.CONFIRMED);

        OrderEntity saved = orderRepository.save(existing);

        publishOrderCreatedEvent(saved, totalAmount);

        return mapper.toResponse(saved);
    }

    // ================= CANCEL =================

    @Override
    @Transactional
    public void cancelOrder(String orderId) {

        UUID id = parseUUID(orderId, ErrorCode.ORDER_INVALID_ID);

        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException(ErrorCode.ORDER_ALREADY_CANCELLED);
        }

        order.setStatus(OrderStatus.CANCELLED);

        orderRepository.save(order);
    }

    // ================= USER =================

    @Override
    public boolean isOrderOwnedByUser(String orderId, String userId) {

        try {
            UUID orderUUID = UUID.fromString(orderId);
            return orderRepository.findById(orderUUID)
                    .map(o -> o.getCustomerId().toString().equals(userId))
                    .orElse(false);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public PageResponse<OrderResponseDto> getOrdersByCustomerId(String customerId, Pageable pageable) {

        UUID id = parseUUID(customerId, ErrorCode.INVALID_USER_ID);

        Page<OrderEntity> page = orderRepository.findByCustomerId(id, pageable);

        return new PageResponse<>(
                page.getContent().stream().map(mapper::toResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    // ================= EVENT HANDLING =================

    @Override
    @Transactional
    public void updateOrderStatus(UUID orderId, UUID eventId, String eventType, String status, String paymentReference) {

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        if (order.getProcessedEventIds().contains(eventId)) {
            return;
        }

        switch (eventType) {

            case "INVENTORY" -> {
                if ("SUCCESS".equals(status)) {
                    order.setStatus(OrderStatus.STOCK_RESERVED);
                } else {
                    order.setStatus(OrderStatus.FAILED);
                }
            }

            case "PAYMENT" -> {
                if ("SUCCESS".equals(status)) {
                    order.setStatus(OrderStatus.CONFIRMED);
                    order.setPaymentReference(paymentReference);
                } else {
                    order.setStatus(OrderStatus.CANCELLED);
                }
            }

            default -> throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        order.getProcessedEventIds().add(eventId);

        orderRepository.save(order);
    }

    // ================= PRIVATE =================

    private ProductResponse getProductOrThrow(UUID productId) {
        try {
            return grpcProductServiceClient.getProduct(productId);
        } catch (StatusRuntimeException e) {
          throw   GrpcExceptionMapper.map(e);
        }
    }

    private void publishOrderCreatedEvent(@NonNull OrderEntity order, BigDecimal totalAmount) {

        List<OrderItemEvent> items = order.getItems().stream()
                .map(i -> new OrderItemEvent(
                        i.getId().getProductId(),
                        i.getQuantity()
                ))
                .toList();

        OrderCreatedEvent event = new OrderCreatedEvent(
                order.getId(),
                order.getCustomerId(),
                totalAmount,
                items
        );

        orderEventProducer.sendOrderCreatedEvent(event);
    }
}