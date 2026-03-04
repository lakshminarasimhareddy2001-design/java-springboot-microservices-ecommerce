package com.eswar.productservice.dto;

import java.time.Instant;
import java.util.UUID;

public record CategoryResponseDto(
        UUID id,
        String name,
        String description,
        Instant createdAt,
        Instant updatedAt
) {
}
