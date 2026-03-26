package com.eswar.orderservice.exceptions;

public class OrderNotFoundException extends BusinessException {
    public OrderNotFoundException(String orderId) {
        super("Order not found: " + orderId, "ORDER_NOT_FOUND");
    }
}
