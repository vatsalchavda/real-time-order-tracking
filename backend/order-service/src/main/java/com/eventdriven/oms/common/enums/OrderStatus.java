package com.eventdriven.oms.common.enums;

/**
 * ENUM CONCEPT:
 * Enums are type-safe constants in Java. They prevent invalid values
 * and make code more readable. In event-driven systems, enums help
 * maintain consistency across services.
 */
public enum OrderStatus {
    PENDING,           // Order created, waiting for inventory check
    CONFIRMED,         // Inventory reserved, order confirmed
    PROCESSING,        // Order being prepared
    SHIPPED,           // Order dispatched
    DELIVERED,         // Order completed
    CANCELLED,         // Order cancelled
    FAILED             // Order failed (e.g., insufficient inventory)
}

// Made with Bob
