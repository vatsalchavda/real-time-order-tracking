package com.eventdriven.oms.common.event;

import com.eventdriven.oms.common.dto.OrderDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * EVENT-DRIVEN ARCHITECTURE CONCEPT:
 * 
 * Events are immutable messages that represent something that has happened.
 * They enable loose coupling between microservices.
 * 
 * KEY PRINCIPLES:
 * 1. Events are past-tense (OrderCreated, not CreateOrder)
 * 2. Events contain all necessary data (no need to query back)
 * 3. Events are immutable (once published, never changed)
 * 4. Events have unique IDs for idempotency and tracing
 * 
 * EVENT SOURCING:
 * All state changes are stored as a sequence of events.
 * The current state can be reconstructed by replaying events.
 * 
 * BENEFITS:
 * - Audit trail (who did what, when)
 * - Time travel (replay to any point)
 * - Event replay for new services
 * - Debugging and troubleshooting
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    
    /**
     * Unique event ID for idempotency.
     * If the same event is received twice, it can be ignored.
     */
    @Builder.Default
    private String eventId = UUID.randomUUID().toString();
    
    /**
     * Event type determines which handler processes it.
     */
    private OrderEventType eventType;
    
    /**
     * The order data at the time of the event.
     */
    private OrderDTO order;
    
    /**
     * Timestamp when the event occurred.
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    /**
     * Service that published the event (for tracing).
     */
    private String source;
    
    /**
     * Correlation ID to track related events across services.
     * All events for the same order share this ID.
     */
    private String correlationId;
    
    /**
     * Additional metadata or error messages.
     */
    private String message;
    
    /**
     * EVENT TYPES:
     * Different types of events in the order lifecycle.
     */
    public enum OrderEventType {
        ORDER_CREATED,           // Order service: New order created
        INVENTORY_CHECK_REQUESTED, // Order service: Request inventory check
        INVENTORY_RESERVED,      // Inventory service: Stock reserved
        INVENTORY_INSUFFICIENT,  // Inventory service: Not enough stock
        ORDER_CONFIRMED,         // Order service: Order confirmed
        ORDER_CANCELLED,         // Order service: Order cancelled
        INVENTORY_RELEASED,      // Inventory service: Stock released
        NOTIFICATION_SENT        // Notification service: User notified
    }
    
    /**
     * Factory method for creating events with correlation ID.
     */
    public static OrderEvent create(OrderEventType type, OrderDTO order, String source) {
        return OrderEvent.builder()
                .eventType(type)
                .order(order)
                .source(source)
                .correlationId(order.getOrderId())
                .build();
    }
}

