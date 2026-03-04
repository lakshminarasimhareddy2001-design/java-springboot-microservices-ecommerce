package com.eswar.productservice.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductResponseDto(

        UUID id,
        String sku,
        String name,
        String description,
        BigDecimal price,
        String status,
        UUID categoryId,
        String categoryName,
        Instant createdAt,
        Instant updatedAt
) {
}
