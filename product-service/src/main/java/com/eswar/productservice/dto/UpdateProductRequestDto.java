package com.eswar.productservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateProductRequestDto(
        @NotBlank(message = "Name is required")
                 String name,

                @Size(max = 1000)
                String description,

@NotNull
@Positive(message = "Price must be positive")
        BigDecimal price
) {
}
