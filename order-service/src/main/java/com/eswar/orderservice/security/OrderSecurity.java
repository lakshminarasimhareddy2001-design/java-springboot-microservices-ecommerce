package com.eswar.orderservice.security;

import com.eswar.orderservice.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderSecurity {

    private final IOrderService orderService;

    public boolean isOwner(String orderId, String userId) {
        return orderService.isOrderOwnedByUser(orderId, userId);
    }
}
