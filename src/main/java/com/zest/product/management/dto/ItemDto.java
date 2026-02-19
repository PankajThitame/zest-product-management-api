package com.zest.product.management.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ItemDto {
    private Long id;
    private Long productId;

    @Positive(message = "Quantity must be positive")
    private Integer quantity;
}
