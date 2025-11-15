package com.eventdriven.oms.orderservice.service;

import com.eventdriven.oms.common.dto.OrderDTO;
import com.eventdriven.oms.common.dto.OrderItemDTO;
import com.eventdriven.oms.common.enums.OrderStatus;
import com.eventdriven.oms.orderservice.event.OrderEventPublisher;
import com.eventdriven.oms.orderservice.model.Order;
import com.eventdriven.oms.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * SERVICE LAYER - BUSINESS LOGIC
 * 
 * The Service layer contains business logic and orchestrates operations.
 * It sits between the Controller (API) and Repository (Data Access).
 * 
 * @Service - Marks this as a service component
 * - Spring creates a singleton instance
 * - Can be injected into controllers and other services
 * - Indicates this class contains business logic
 * 
 * LAYERED ARCHITECTURE:
 * Controller -> Service -> Repository -> Database
 * 
 * Controller: Handles HTTP requests/responses, validation
 * Service: Business logic, transaction management, event publishing
 * Repository: Data access, database queries
 * 
 * COMPARISON WITH EXPRESS.JS:
 * In Express, you might have:
 *   routes/orderRoutes.js -> controllers/orderController.js -> services/orderService.js -> models/Order.js
 * 
 * In Spring Boot:
 *   OrderController -> OrderService -> OrderRepository -> Order entity
 * 
 * The pattern is similar, but Spring provides more structure and features.
 * 
 * @Transactional - Database transaction management
 * - Ensures all database operations succeed or all fail (ACID properties)
 * - Automatically rolls back on exceptions
 * - Can span multiple repository calls
 * 
 * Example:
 * @Transactional
 * public void transferMoney(String from, String to, double amount) {
 *     accountRepo.debit(from, amount);  // If this succeeds...
 *     accountRepo.credit(to, amount);   // ...but this fails...
 *     // Both operations are rolled back automatically!
 * }
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    
    /**
     * DEPENDENCY INJECTION:
     * These dependencies are injected via constructor (thanks to @RequiredArgsConstructor)
     */
    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;
    
    /**
     * CREATE ORDER
     * 
     * This method demonstrates the Saga pattern for distributed transactions.
     * 
     * SAGA PATTERN:
     * In microservices, you can't use traditional database transactions across services.
     * The Saga pattern coordinates distributed transactions using events.
     * 
     * Steps:
     * 1. Create order in PENDING status
     * 2. Publish ORDER_CREATED event
     * 3. Publish INVENTORY_CHECK_REQUESTED event
     * 4. Wait for Inventory Service to respond
     * 5. If inventory available -> confirm order
     * 6. If inventory insufficient -> cancel order (compensation)
     * 
     * This is called "Choreography-based Saga" - services react to events.
     * Alternative: "Orchestration-based Saga" - central coordinator manages the flow.
     */
    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        log.info("Creating order for customer: {}", orderDTO.getCustomerId());
        
        // Convert DTO to Entity
        Order order = mapToEntity(orderDTO);
        
        // Set initial values
        order.setId(UUID.randomUUID().toString());
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.calculateTotalAmount();
        
        // Save to database
        Order savedOrder = orderRepository.save(order);
        log.info("Order created with ID: {}", savedOrder.getId());
        
        // Convert back to DTO
        OrderDTO savedOrderDTO = mapToDTO(savedOrder);
        
        // Publish events (Event-Driven Architecture)
        eventPublisher.publishOrderCreated(savedOrderDTO);
        eventPublisher.publishInventoryCheckRequested(savedOrderDTO);
        
        /**
         * EVENTUAL CONSISTENCY:
         * The order is saved immediately, but confirmation happens asynchronously.
         * The client receives a 202 Accepted response (not 200 OK).
         * 
         * This is different from synchronous REST calls:
         * Synchronous: POST /orders -> Check inventory -> Return result (slow, coupled)
         * Asynchronous: POST /orders -> Return immediately -> Process in background (fast, decoupled)
         */
        
        return savedOrderDTO;
    }
    
    /**
     * CONFIRM ORDER
     * 
     * Called when Inventory Service confirms stock is reserved.
     */
    @Transactional
    public void confirmOrder(String orderId) {
        log.info("Confirming order: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        
        order.updateStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);
        
        // Publish confirmation event
        eventPublisher.publishOrderConfirmed(mapToDTO(order));
        
        log.info("Order confirmed: {}", orderId);
    }
    
    /**
     * CANCEL ORDER
     * 
     * Called when inventory is insufficient or order is cancelled by user.
     * This is the compensation step in the Saga pattern.
     */
    @Transactional
    public void cancelOrder(String orderId, String reason) {
        log.info("Cancelling order: {} - Reason: {}", orderId, reason);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        
        order.updateStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        
        // Publish cancellation event
        eventPublisher.publishOrderCancelled(mapToDTO(order), reason);
        
        log.info("Order cancelled: {}", orderId);
    }
    
    /**
     * GET ORDER BY ID
     */
    public OrderDTO getOrderById(String orderId) {
        log.info("Fetching order: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        
        return mapToDTO(order);
    }
    
    /**
     * GET ALL ORDERS
     */
    public List<OrderDTO> getAllOrders() {
        log.info("Fetching all orders");
        
        return orderRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * GET ORDERS BY CUSTOMER
     */
    public List<OrderDTO> getOrdersByCustomer(String customerId) {
        log.info("Fetching orders for customer: {}", customerId);
        
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * MAPPER METHODS
     * 
     * These convert between Entity (database) and DTO (API).
     * 
     * WHY SEPARATE ENTITY AND DTO?
     * 1. Decoupling - Change database schema without affecting API
     * 2. Security - Don't expose internal fields (e.g., passwords)
     * 3. Flexibility - API can have different structure than database
     * 4. Versioning - Support multiple API versions with same entity
     * 
     * In production, use MapStruct or ModelMapper for automatic mapping.
     */
    private OrderDTO mapToDTO(Order order) {
        return OrderDTO.builder()
                .orderId(order.getId())
                .customerId(order.getCustomerId())
                .customerName(order.getCustomerName())
                .items(order.getItems().stream()
                        .map(item -> OrderItemDTO.builder()
                                .productId(item.getProductId())
                                .productName(item.getProductName())
                                .quantity(item.getQuantity())
                                .price(item.getPrice())
                                .build())
                        .collect(Collectors.toList()))
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .shippingAddress(order.getShippingAddress())
                .build();
    }
    
    private Order mapToEntity(OrderDTO dto) {
        return Order.builder()
                .id(dto.getOrderId())
                .customerId(dto.getCustomerId())
                .customerName(dto.getCustomerName())
                .items(dto.getItems().stream()
                        .map(item -> Order.OrderItem.builder()
                                .productId(item.getProductId())
                                .productName(item.getProductName())
                                .quantity(item.getQuantity())
                                .price(item.getPrice())
                                .build())
                        .collect(Collectors.toList()))
                .totalAmount(dto.getTotalAmount())
                .status(dto.getStatus())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .shippingAddress(dto.getShippingAddress())
                .build();
    }
}

