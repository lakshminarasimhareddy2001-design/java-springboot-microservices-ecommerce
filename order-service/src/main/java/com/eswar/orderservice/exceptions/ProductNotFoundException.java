package com.eswar.orderservice.exceptions;

public class ProductNotFoundException extends BusinessException {
    public ProductNotFoundException(String productId) {

        super("Product not found: " + productId, "PRODUCT_NOT_FOUND");
    }
}
