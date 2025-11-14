package com.eventdriven.oms.orderservice.model;

import com.eventdriven.oms.common.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ENTITY/MODEL CLASS CONCEPT:
 * 
 * This represents the domain model - the core business object.
 * In Spring Data MongoDB, entities are mapped to MongoDB collections.
 * 
 * @Document - Marks this class as a MongoDB document
 * - Maps to a collection named "orders" (by default, class name in lowercase)
 * - Similar to @Entity in JPA/Hibernate for SQL databases
 * 
 * COMPARISON WITH EXPRESS/MONGOOSE:
 * In Mongoose (Node.js):
 *   const orderSchema = new Schema({
 *     customerId: String,
 *     items: [itemSchema],
 *     status: String
 *   });
 * 
 * In Spring Data MongoDB:
 *   - Use @Document annotation
 *   - Define fields as Java class properties
 *   - Spring handles the mapping automatically
 * 
 * KEY DIFFERENCES FROM DTO:
 * - Entity: Internal domain model, includes database-specific annotations
 * - DTO: Data transfer object for API communication, no database annotations
 * - Entities should NOT be exposed directly in REST APIs (use DTOs instead)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")  // MongoDB collection name
public class Order {
    
    /**
     * @Id - Marks this field as the primary key
     * MongoDB uses String IDs by default (ObjectId converted to String)
     * Spring Data MongoDB auto-generates IDs if not provided
     */
    @Id
    private String id;
    
    private String customerId;
    private String customerName;
    private List<OrderItem> items;
    private Double totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String shippingAddress;
    
    /**
     * EMBEDDED DOCUMENT:
     * OrderItem is embedded within Order (not a separate collection)
     * This is denormalization - common in NoSQL for performance
     * 
     * In SQL, you'd have a separate order_items table with foreign key.
     * In MongoDB, we embed related data for faster reads.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItem {
        private String productId;
        private String productName;
        private Integer quantity;
        private Double price;
        
        public Double getSubtotal() {
            return quantity * price;
        }
    }
    
    /**
     * BUSINESS LOGIC METHODS:
     * Domain models can contain business logic (Domain-Driven Design)
     */
    public void calculateTotalAmount() {
        this.totalAmount = items.stream()
                .mapToDouble(OrderItem::getSubtotal)
                .sum();
    }
    
    public void updateStatus(OrderStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * LIFECYCLE HOOKS:
     * You can add methods that run before/after save operations
     * (requires @PrePersist, @PreUpdate annotations from javax.persistence)
     */
}

// Made with Bob
