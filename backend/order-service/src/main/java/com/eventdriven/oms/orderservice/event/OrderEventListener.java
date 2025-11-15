package com.eventdriven.oms.orderservice.event;

import com.eventdriven.oms.common.event.OrderEvent;
import com.eventdriven.oms.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

/**
 * EVENT LISTENER - CONSUMING EVENTS FROM MESSAGE BROKER
 * 
 * This class listens for events published by other services.
 * 
 * @Configuration - Marks this class as a source of bean definitions
 * - Spring will scan this class for @Bean methods
 * - Beans defined here are registered in the ApplicationContext
 * 
 * SPRING CLOUD STREAM FUNCTIONAL PROGRAMMING MODEL:
 * 
 * Spring Cloud Stream 3.x+ uses Java 8 functional interfaces:
 * - Supplier<T> - Produces messages (no input, returns output)
 * - Function<T, R> - Transforms messages (input -> output)
 * - Consumer<T> - Consumes messages (input, no output)
 * 
 * WHY FUNCTIONAL APPROACH?
 * 1. More concise than @StreamListener (deprecated)
 * 2. Better testability (pure functions)
 * 3. Reactive programming support (Flux, Mono)
 * 4. Easier to compose and chain
 * 
 * COMPARISON WITH EXPRESS/NODE.JS:
 * In Node.js with RabbitMQ:
 *   channel.consume('inventory-events', (msg) => {
 *     const event = JSON.parse(msg.content.toString());
 *     handleInventoryEvent(event);
 *     channel.ack(msg);
 *   });
 * 
 * In Spring Cloud Stream:
 *   @Bean
 *   public Consumer<OrderEvent> inventoryEventConsumer() {
 *     return event -> handleInventoryEvent(event);
 *   }
 *   // Spring handles deserialization, acknowledgment, error handling!
 * 
 * BINDING CONFIGURATION:
 * The bean name maps to a binding in application.yml:
 * 
 * spring:
 *   cloud:
 *     function:
 *       definition: inventoryEventConsumer  # Bean name
 *     stream:
 *       bindings:
 *         inventoryEventConsumer-in-0:  # Bean name + "-in-0"
 *           destination: order.events
 *           group: order-service  # Consumer group for load balancing
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {
    
    private final OrderService orderService;
    
    /**
     * INVENTORY EVENT CONSUMER
     * 
     * Listens for events from the Inventory Service.
     * 
     * @Bean - Registers this Consumer as a Spring bean
     * - Spring Cloud Stream automatically binds it to a message channel
     * - The bean name determines the binding configuration
     * 
     * Consumer<OrderEvent> - Functional interface that accepts OrderEvent
     * - Called automatically when a message arrives
     * - Spring deserializes JSON to OrderEvent
     * 
     * CONSUMER GROUPS:
     * Multiple instances of the same service share the consumer group.
     * Each message is delivered to only ONE instance (load balancing).
     * 
     * Example with 3 instances:
     * Instance 1: order-service (group: order-service)
     * Instance 2: order-service (group: order-service)
     * Instance 3: order-service (group: order-service)
     * 
     * Message arrives -> RabbitMQ delivers to ONE instance only
     * This enables horizontal scaling!
     */
    @Bean
    public Consumer<OrderEvent> inventoryEventConsumer() {
        return event -> {
            log.info("Received event: {} for order: {}", 
                     event.getEventType(), 
                     event.getOrder().getOrderId());
            
            /**
             * EVENT ROUTING:
             * Based on event type, call appropriate service method.
             * This is the Saga pattern - coordinating distributed transactions.
             */
            switch (event.getEventType()) {
                case INVENTORY_RESERVED:
                    // Inventory successfully reserved, confirm the order
                    log.info("Inventory reserved for order: {}", event.getOrder().getOrderId());
                    orderService.confirmOrder(event.getOrder().getOrderId());
                    break;
                    
                case INVENTORY_INSUFFICIENT:
                    // Not enough inventory, cancel the order
                    log.warn("Insufficient inventory for order: {}", event.getOrder().getOrderId());
                    orderService.cancelOrder(
                        event.getOrder().getOrderId(), 
                        "Insufficient inventory"
                    );
                    break;
                    
                default:
                    log.debug("Ignoring event type: {}", event.getEventType());
            }
            
            /**
             * MESSAGE ACKNOWLEDGMENT:
             * Spring Cloud Stream automatically acknowledges messages after successful processing.
             * If an exception is thrown, the message is NOT acknowledged and can be retried.
             * 
             * Acknowledgment modes:
             * 1. AUTO (default) - Ack after successful processing
             * 2. MANUAL - You control when to ack
             * 3. NONE - No acknowledgment (fire and forget)
             */
        };
    }
    
    /**
     * ERROR HANDLING:
     * 
     * If an exception occurs in the consumer:
     * 1. Message is NOT acknowledged
     * 2. Spring Cloud Stream retries based on configuration
     * 3. After max retries, message goes to Dead Letter Queue (DLQ)
     * 
     * Configuration in application.yml:
     * spring:
     *   cloud:
     *     stream:
     *       bindings:
     *         inventoryEventConsumer-in-0:
     *           consumer:
     *             maxAttempts: 3
     *             backOffInitialInterval: 1000
     *             backOffMaxInterval: 10000
     *       rabbit:
     *         bindings:
     *           inventoryEventConsumer-in-0:
     *             consumer:
     *               autoBindDlq: true
     *               republishToDlq: true
     */
    
    /**
     * IDEMPOTENCY:
     * 
     * Events may be delivered more than once (at-least-once delivery).
     * Your event handlers should be idempotent (safe to process multiple times).
     * 
     * Strategies:
     * 1. Check event ID before processing (store processed IDs)
     * 2. Use database constraints (unique keys)
     * 3. Design operations to be naturally idempotent
     * 
     * Example:
     * if (eventAlreadyProcessed(event.getEventId())) {
     *     log.info("Event already processed, skipping: {}", event.getEventId());
     *     return;
     * }
     * processEvent(event);
     * markEventAsProcessed(event.getEventId());
     */
    
    /**
     * REACTIVE PROGRAMMING (ADVANCED):
     * 
     * For high-throughput scenarios, use reactive types:
     * 
     * @Bean
     * public Function<Flux<OrderEvent>, Mono<Void>> reactiveConsumer() {
     *     return flux -> flux
     *         .doOnNext(event -> log.info("Processing: {}", event))
     *         .flatMap(event -> orderService.processEventAsync(event))
     *         .then();
     * }
     * 
     * Benefits:
     * - Non-blocking I/O
     * - Backpressure handling
     * - Better resource utilization
     */
}

