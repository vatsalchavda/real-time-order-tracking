package com.eventdriven.oms.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ORDER ITEM DTO:
 * Represents individual items within an order.
 * This demonstrates composition in DTOs.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    
    @NotBlank(message = "Product ID is required")
    private String productId;
    
    @NotBlank(message = "Product name is required")
    private String productName;
    
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
    
    @Positive(message = "Price must be positive")
    private Double price;
    
    // Calculated field
    public Double getSubtotal() {
        return quantity * price;
    }
}

// Made with Bob
