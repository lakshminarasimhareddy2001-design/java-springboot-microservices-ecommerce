package com.eswar.orderservice.service;

import com.eswar.orderservice.dto.OrderDto;
import com.eswar.orderservice.dto.OrderResponseDto;
import com.eswar.orderservice.dto.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.security.Principal;
import java.util.List;


public interface IOrderService {
    OrderResponseDto createOrder(OrderDto dto, Principal principal);
    PageResponse<OrderResponseDto> getALlOrders(Pageable pageable);
    OrderResponseDto getOrderById(String orderId);
    OrderResponseDto updateOrder(String orderId, OrderDto orderDto);
    void cancelOrder(String orderId);
    boolean isOrderOwnedByUser(String orderId, String userId);
    PageResponse<OrderResponseDto> getOrdersByCustomerId(String customerId,Pageable pageable);

}
