package com.eventdriven.oms.common.dto;

import com.eventdriven.oms.common.enums.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO (Data Transfer Object) CONCEPT:
 * DTOs are used to transfer data between layers and services.
 * They decouple the internal domain model from external APIs.
 * 
 * LOMBOK ANNOTATIONS:
 * @Data - Generates getters, setters, toString, equals, and hashCode
 * @Builder - Implements the Builder pattern for object creation
 * @NoArgsConstructor - Generates a no-argument constructor (required for Jackson)
 * @AllArgsConstructor - Generates a constructor with all fields
 * 
 * VALIDATION ANNOTATIONS:
 * @NotBlank - Ensures string is not null or empty
 * @NotNull - Ensures field is not null
 * @Positive - Ensures number is positive
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    
    private String orderId;
    
    @NotBlank(message = "Customer ID is required")
    private String customerId;
    
    @NotBlank(message = "Customer name is required")
    private String customerName;
    
    @NotNull(message = "Order items are required")
    private List<OrderItemDTO> items;
    
    @Positive(message = "Total amount must be positive")
    private Double totalAmount;
    
    private OrderStatus status;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private String shippingAddress;
    
    /**
     * Builder Pattern Example:
     * OrderDTO order = OrderDTO.builder()
     *     .customerId("CUST123")
     *     .customerName("John Doe")
     *     .items(itemsList)
     *     .totalAmount(99.99)
     *     .status(OrderStatus.PENDING)
     *     .build();
     */
}

// Made with Bob
