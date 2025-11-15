package com.eventdriven.oms.inventoryservice.event;

import com.eventdriven.oms.common.event.OrderEvent;
import com.eventdriven.oms.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

/**
 * INVENTORY EVENT LISTENER
 * 
 * Demonstrates Event-Driven Architecture patterns:
 * - Event Choreography (services react to events independently)
 * - Saga Pattern (distributed transaction coordination)
 * - Eventual Consistency (async processing)
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class InventoryEventListener {
    
    private final InventoryService inventoryService;
    
    @Bean
    public Consumer<OrderEvent> orderEventConsumer() {
        return event -> {
            log.info("Received event: {} for order: {}", 
                     event.getEventType(), 
                     event.getOrder().getOrderId());
            
            if (event.getEventType() == OrderEvent.OrderEventType.INVENTORY_CHECK_REQUESTED) {
                // Check and reserve inventory
                inventoryService.processInventoryCheck(event.getOrder());
            } else if (event.getEventType() == OrderEvent.OrderEventType.ORDER_CANCELLED) {
                // Release reserved inventory (compensation)
                inventoryService.releaseInventory(event.getOrder());
            }
        };
    }
}

