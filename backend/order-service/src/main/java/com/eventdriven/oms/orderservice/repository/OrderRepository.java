package com.eventdriven.oms.orderservice.repository;

import com.eventdriven.oms.common.enums.OrderStatus;
import com.eventdriven.oms.orderservice.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REPOSITORY PATTERN CONCEPT:
 * 
 * The Repository pattern abstracts data access logic.
 * It provides a collection-like interface for accessing domain objects.
 * 
 * @Repository - Marks this as a Data Access Object (DAO)
 * - Spring creates a proxy implementation at runtime
 * - Provides exception translation (converts database exceptions to Spring exceptions)
 * 
 * SPRING DATA MONGODB:
 * By extending MongoRepository<Order, String>:
 * - Order: The entity type
 * - String: The ID type
 * 
 * You get these methods for FREE (no implementation needed):
 * - save(Order order)
 * - findById(String id)
 * - findAll()
 * - deleteById(String id)
 * - count()
 * - existsById(String id)
 * 
 * COMPARISON WITH EXPRESS/MONGOOSE:
 * In Mongoose:
 *   const order = await Order.findById(id);
 *   await order.save();
 * 
 * In Spring Data:
 *   Order order = orderRepository.findById(id).orElseThrow();
 *   orderRepository.save(order);
 * 
 * QUERY METHODS:
 * Spring Data generates queries from method names automatically!
 * 
 * Method naming convention:
 * - findBy + PropertyName + Condition
 * - Examples:
 *   findByCustomerId -> WHERE customerId = ?
 *   findByStatusAndCreatedAtAfter -> WHERE status = ? AND createdAt > ?
 *   findByCustomerIdOrderByCreatedAtDesc -> WHERE customerId = ? ORDER BY createdAt DESC
 * 
 * This is called "Query Derivation" - Spring parses the method name
 * and generates the MongoDB query automatically!
 */
@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    
    /**
     * DERIVED QUERY METHODS:
     * Spring Data automatically implements these based on method names.
     * No need to write any implementation code!
     */
    
    // Find all orders for a specific customer
    // Generated query: db.orders.find({ customerId: customerId })
    List<Order> findByCustomerId(String customerId);
    
    // Find orders by status
    // Generated query: db.orders.find({ status: status })
    List<Order> findByStatus(OrderStatus status);
    
    // Find orders by customer and status
    // Generated query: db.orders.find({ customerId: customerId, status: status })
    List<Order> findByCustomerIdAndStatus(String customerId, OrderStatus status);
    
    // Find orders created after a specific date
    // Generated query: db.orders.find({ createdAt: { $gt: date } })
    List<Order> findByCreatedAtAfter(LocalDateTime date);
    
    // Find orders with sorting
    // Generated query: db.orders.find({ customerId: customerId }).sort({ createdAt: -1 })
    List<Order> findByCustomerIdOrderByCreatedAtDesc(String customerId);
    
    /**
     * CUSTOM QUERIES:
     * If method name queries aren't enough, you can use @Query annotation:
     * 
     * @Query("{ 'totalAmount': { $gte: ?0, $lte: ?1 } }")
     * List<Order> findByTotalAmountBetween(Double min, Double max);
     * 
     * Or use MongoTemplate for complex queries (injected in Service layer)
     */
    
    /**
     * KEY BENEFITS:
     * 1. No boilerplate code - Spring generates implementations
     * 2. Type-safe - Compile-time checking
     * 3. Consistent API across different databases (JPA, MongoDB, etc.)
     * 4. Easy to test - Can mock the repository interface
     * 5. Transaction support (if needed)
     */
}

