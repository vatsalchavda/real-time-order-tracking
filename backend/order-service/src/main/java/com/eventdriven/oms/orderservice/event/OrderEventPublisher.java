package com.eventdriven.oms.orderservice.event;

import com.eventdriven.oms.common.dto.OrderDTO;
import com.eventdriven.oms.common.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

/**
 * EVENT PUBLISHER - CORE OF EVENT-DRIVEN ARCHITECTURE
 * 
 * This class is responsible for publishing events to the message broker.
 * 
 * @Component - Marks this as a Spring-managed bean
 * - Spring will create an instance and manage its lifecycle
 * - Can be injected into other classes using @Autowired
 * 
 * @RequiredArgsConstructor (Lombok) - Generates constructor for final fields
 * - Enables constructor-based dependency injection
 * - Preferred over @Autowired field injection (more testable)
 * 
 * @Slf4j (Lombok) - Generates a logger field automatically
 * - Equivalent to: private static final Logger log = LoggerFactory.getLogger(OrderEventPublisher.class);
 * 
 * SPRING CLOUD STREAM CONCEPT:
 * 
 * Spring Cloud Stream is an abstraction over message brokers (RabbitMQ, Kafka, etc.)
 * It provides a consistent programming model regardless of the underlying broker.
 * 
 * KEY COMPONENTS:
 * 1. StreamBridge - Programmatic way to send messages to any destination
 * 2. Binder - Connects to the actual message broker (RabbitMQ, Kafka)
 * 3. Binding - Maps application channels to broker topics/queues
 * 
 * COMPARISON WITH EXPRESS/NODE.JS:
 * In Node.js with RabbitMQ:
 *   const amqp = require('amqplib');
 *   const connection = await amqp.connect('amqp://localhost');
 *   const channel = await connection.createChannel();
 *   await channel.assertQueue('orders');
 *   channel.sendToQueue('orders', Buffer.from(JSON.stringify(order)));
 * 
 * In Spring Cloud Stream:
 *   streamBridge.send("orders-out-0", event);
 *   // Spring handles connection, serialization, error handling automatically!
 * 
 * BENEFITS:
 * - Broker-agnostic (switch from RabbitMQ to Kafka without code changes)
 * - Automatic serialization/deserialization
 * - Built-in error handling and retry logic
 * - Partitioning and consumer groups support
 * - Health checks and metrics
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {
    
    /**
     * DEPENDENCY INJECTION:
     * StreamBridge is injected by Spring automatically.
     * The 'final' keyword + @RequiredArgsConstructor creates constructor injection.
     * 
     * Why constructor injection?
     * - Immutable (thread-safe)
     * - Easy to test (can pass mocks in constructor)
     * - Makes dependencies explicit
     * - Prevents NullPointerException
     */
    private final StreamBridge streamBridge;
    
    /**
     * PUBLISH ORDER CREATED EVENT
     * 
     * This method publishes an event when a new order is created.
     * 
     * BINDING NAME CONVENTION:
     * "orderEvents-out-0" breaks down as:
     * - orderEvents: The binding name (configured in application.yml)
     * - out: Output binding (sending messages)
     * - 0: Index (for multiple outputs, use 0, 1, 2, etc.)
     * 
     * In application.yml, this maps to:
     * spring:
     *   cloud:
     *     stream:
     *       bindings:
     *         orderEvents-out-0:
     *           destination: order.events  # RabbitMQ exchange or Kafka topic
     * 
     * EVENT FLOW:
     * 1. Service calls publishOrderCreated()
     * 2. Event is serialized to JSON
     * 3. StreamBridge sends to configured destination
     * 4. Message broker (RabbitMQ) receives and routes to subscribers
     * 5. Other services (Inventory, Notification) receive and process
     */
    public void publishOrderCreated(OrderDTO order) {
        OrderEvent event = OrderEvent.create(
            OrderEvent.OrderEventType.ORDER_CREATED,
            order,
            "order-service"
        );
        
        log.info("Publishing ORDER_CREATED event for order: {}", order.getOrderId());
        
        // Send event to message broker
        streamBridge.send("orderEvents-out-0", event);
        
        /**
         * WHAT HAPPENS BEHIND THE SCENES:
         * 1. Spring Cloud Stream serializes the event to JSON using Jackson
         * 2. Adds message headers (timestamp, content-type, etc.)
         * 3. Sends to RabbitMQ exchange "order.events"
         * 4. RabbitMQ routes to queues based on routing keys
         * 5. Subscribing services receive the message
         * 6. Spring Cloud Stream deserializes JSON back to OrderEvent
         * 7. Calls the appropriate @StreamListener or Consumer function
         */
    }
    
    /**
     * PUBLISH ORDER CONFIRMED EVENT
     * 
     * Called when inventory is successfully reserved.
     */
    public void publishOrderConfirmed(OrderDTO order) {
        OrderEvent event = OrderEvent.create(
            OrderEvent.OrderEventType.ORDER_CONFIRMED,
            order,
            "order-service"
        );
        
        log.info("Publishing ORDER_CONFIRMED event for order: {}", order.getOrderId());
        streamBridge.send("orderEvents-out-0", event);
    }
    
    /**
     * PUBLISH ORDER CANCELLED EVENT
     * 
     * Called when order is cancelled (e.g., insufficient inventory).
     */
    public void publishOrderCancelled(OrderDTO order, String reason) {
        OrderEvent event = OrderEvent.create(
            OrderEvent.OrderEventType.ORDER_CANCELLED,
            order,
            "order-service"
        );
        event.setMessage(reason);
        
        log.info("Publishing ORDER_CANCELLED event for order: {} - Reason: {}", 
                 order.getOrderId(), reason);
        streamBridge.send("orderEvents-out-0", event);
    }
    
    /**
     * PUBLISH INVENTORY CHECK REQUESTED EVENT
     * 
     * Requests inventory service to check and reserve stock.
     * This demonstrates the Saga pattern for distributed transactions.
     */
    public void publishInventoryCheckRequested(OrderDTO order) {
        OrderEvent event = OrderEvent.create(
            OrderEvent.OrderEventType.INVENTORY_CHECK_REQUESTED,
            order,
            "order-service"
        );
        
        log.info("Publishing INVENTORY_CHECK_REQUESTED event for order: {}", order.getOrderId());
        streamBridge.send("orderEvents-out-0", event);
    }
    
    /**
     * ERROR HANDLING:
     * 
     * Spring Cloud Stream provides built-in error handling:
     * 1. Retry mechanism (configurable)
     * 2. Dead Letter Queue (DLQ) for failed messages
     * 3. Error channels for custom error handling
     * 
     * Configuration in application.yml:
     * spring:
     *   cloud:
     *     stream:
     *       bindings:
     *         orderEvents-out-0:
     *           producer:
     *             errorChannelEnabled: true
     *       rabbit:
     *         bindings:
     *           orderEvents-out-0:
     *             producer:
     *               autoBindDlq: true
     *               republishToDlq: true
     */
}

