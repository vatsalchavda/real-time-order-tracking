package com.eventdriven.oms.orderservice.controller;

import com.eventdriven.oms.common.dto.OrderDTO;
import com.eventdriven.oms.common.enums.OrderStatus;
import com.eventdriven.oms.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST CONTROLLER - API LAYER
 * 
 * The Controller handles HTTP requests and responses.
 * It's the entry point for external clients (frontend, mobile apps, other services).
 * 
 * @RestController - Combines @Controller and @ResponseBody
 * - @Controller: Marks this as a Spring MVC controller
 * - @ResponseBody: Automatically serializes return values to JSON
 * 
 * Without @RestController, you'd need:
 * @Controller
 * public class OrderController {
 *     @ResponseBody
 *     public OrderDTO getOrder() { ... }
 * }
 * 
 * @RequestMapping - Base path for all endpoints in this controller
 * - All methods inherit this path
 * - Example: /api/orders/123, /api/orders/customer/456
 * 
 * COMPARISON WITH EXPRESS.JS:
 * In Express:
 *   const router = express.Router();
 *   router.post('/orders', createOrder);
 *   router.get('/orders/:id', getOrder);
 *   app.use('/api', router);
 * 
 * In Spring Boot:
 *   @RestController
 *   @RequestMapping("/api/orders")
 *   public class OrderController {
 *       @PostMapping
 *       public OrderDTO createOrder() { ... }
 *       
 *       @GetMapping("/{id}")
 *       public OrderDTO getOrder(@PathVariable String id) { ... }
 *   }
 * 
 * KEY DIFFERENCES:
 * 1. Spring uses annotations instead of method calls
 * 2. Spring automatically handles serialization/deserialization
 * 3. Spring provides built-in validation
 * 4. Spring has better type safety
 * 
 * HTTP STATUS CODES:
 * - 200 OK: Successful GET, PUT, PATCH
 * - 201 Created: Successful POST (resource created)
 * - 202 Accepted: Request accepted for async processing
 * - 204 No Content: Successful DELETE
 * - 400 Bad Request: Invalid input
 * - 404 Not Found: Resource doesn't exist
 * - 500 Internal Server Error: Server error
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")  // Allow requests from any origin (for development)
@Tag(name = "Order Management", description = "APIs for managing orders in the system")
public class OrderController {
    
    private final OrderService orderService;
    
    /**
     * CREATE ORDER ENDPOINT
     * 
     * POST /api/orders
     * 
     * @PostMapping - Handles HTTP POST requests
     * - Used for creating new resources
     * - Request body contains the data
     * 
     * @RequestBody - Binds HTTP request body to method parameter
     * - Spring automatically deserializes JSON to OrderDTO
     * - Uses Jackson library under the hood
     * 
     * @Valid - Triggers validation on the DTO
     * - Checks @NotNull, @NotBlank, @Positive annotations
     * - Returns 400 Bad Request if validation fails
     * - Validation errors are automatically formatted in response
     * 
     * ResponseEntity<T> - Wrapper for HTTP response
     * - Allows setting status code, headers, body
     * - More flexible than returning DTO directly
     * 
     * Example request:
     * POST /api/orders
     * Content-Type: application/json
     * {
     *   "customerId": "CUST123",
     *   "customerName": "John Doe",
     *   "items": [
     *     {
     *       "productId": "PROD001",
     *       "productName": "Laptop",
     *       "quantity": 1,
     *       "price": 999.99
     *     }
     *   ],
     *   "shippingAddress": "123 Main St"
     * }
     * 
     * Response: 202 Accepted
     * {
     *   "orderId": "uuid-here",
     *   "status": "PENDING",
     *   "totalAmount": 999.99,
     *   ...
     * }
     */
    @PostMapping
    @Operation(
        summary = "Create a new order",
        description = "Creates a new order and publishes an OrderCreated event to RabbitMQ for downstream processing"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Order accepted for processing",
                    content = @Content(schema = @Schema(implementation = OrderDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<OrderDTO> createOrder(
            @Parameter(description = "Order details", required = true)
            @Valid @RequestBody OrderDTO orderDTO) {
        log.info("Received request to create order for customer: {}", orderDTO.getCustomerId());
        
        OrderDTO createdOrder = orderService.createOrder(orderDTO);
        
        /**
         * HTTP 202 ACCEPTED:
         * Used for asynchronous operations.
         * The order is created, but processing (inventory check) happens in background.
         * 
         * This is better than 201 Created because:
         * - Client knows the request is accepted but not complete
         * - Follows REST best practices for async operations
         * - Sets proper expectations for eventual consistency
         */
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(createdOrder);
    }
    
    /**
     * GET ORDER BY ID
     * 
     * GET /api/orders/{id}
     * 
     * @GetMapping - Handles HTTP GET requests
     * - Used for retrieving resources
     * - Should be idempotent (multiple calls return same result)
     * 
     * @PathVariable - Binds URL path variable to method parameter
     * - {id} in URL maps to String id parameter
     * - Spring automatically converts types (String, Long, UUID, etc.)
     * 
     * Example: GET /api/orders/abc-123
     * -> id = "abc-123"
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get order by ID",
        description = "Retrieves a specific order by its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order found",
                    content = @Content(schema = @Schema(implementation = OrderDTO.class))),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<OrderDTO> getOrderById(
            @Parameter(description = "Order ID", required = true, example = "507f1f77bcf86cd799439011")
            @PathVariable String id) {
        log.info("Received request to get order: {}", id);
        
        OrderDTO order = orderService.getOrderById(id);
        
        return ResponseEntity.ok(order);  // HTTP 200 OK
    }
    
    /**
     * GET ALL ORDERS
     * 
     * GET /api/orders
     * 
     * Returns a list of all orders.
     * In production, you'd add pagination:
     * 
     * @GetMapping
     * public ResponseEntity<Page<OrderDTO>> getAllOrders(
     *     @RequestParam(defaultValue = "0") int page,
     *     @RequestParam(defaultValue = "10") int size
     * ) {
     *     Pageable pageable = PageRequest.of(page, size);
     *     Page<OrderDTO> orders = orderService.getAllOrders(pageable);
     *     return ResponseEntity.ok(orders);
     * }
     */
    @GetMapping
    @Operation(
        summary = "Get all orders",
        description = "Retrieves a list of all orders in the system"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        log.info("Received request to get all orders");
        
        List<OrderDTO> orders = orderService.getAllOrders();
        
        return ResponseEntity.ok(orders);
    }
    
    /**
     * GET ORDERS BY CUSTOMER
     * 
     * GET /api/orders/customer/{customerId}
     * 
     * @RequestParam vs @PathVariable:
     * - @PathVariable: Part of URL path (/orders/{id})
     * - @RequestParam: Query parameter (/orders?customerId=123)
     * 
     * Use @PathVariable for resource identification
     * Use @RequestParam for filtering, sorting, pagination
     */
    @GetMapping("/customer/{customerId}")
    @Operation(
        summary = "Get orders by customer",
        description = "Retrieves all orders for a specific customer"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<OrderDTO>> getOrdersByCustomer(
            @Parameter(description = "Customer ID", required = true, example = "CUST123")
            @PathVariable String customerId) {
        log.info("Received request to get orders for customer: {}", customerId);
        
        List<OrderDTO> orders = orderService.getOrdersByCustomer(customerId);
        
        return ResponseEntity.ok(orders);
    }
    
    /**
     * UPDATE ORDER STATUS
     *
     * PATCH /api/orders/{id}/status?status=CONFIRMED
     *
     * @PatchMapping - Handles HTTP PATCH requests
     * - Used for partial updates
     * - Only updates specific fields (status in this case)
     *
     * @RequestParam - Binds query parameter to method parameter
     * - ?status=CONFIRMED maps to OrderStatus status parameter
     * - Spring automatically converts String to Enum
     *
     * This endpoint is useful for:
     * - Manual status changes during demo
     * - Admin operations
     * - Testing event flow
     */
    @PatchMapping("/{id}/status")
    @Operation(
        summary = "Update order status",
        description = "Updates the status of an existing order. Useful for manual status changes and testing."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order status updated successfully",
                    content = @Content(schema = @Schema(implementation = OrderDTO.class))),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "400", description = "Invalid status value"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @Parameter(description = "Order ID", required = true, example = "507f1f77bcf86cd799439011")
            @PathVariable String id,
            @Parameter(description = "New order status", required = true, example = "CONFIRMED")
            @RequestParam OrderStatus status) {
        log.info("Received request to update order {} status to {}", id, status);
        
        OrderDTO updatedOrder = orderService.updateOrderStatus(id, status);
        
        return ResponseEntity.ok(updatedOrder);
    }
    
    /**
     * DELETE ORDER
     *
     * DELETE /api/orders/{id}
     *
     * @DeleteMapping - Handles HTTP DELETE requests
     * - Used for deleting resources
     * - Should be idempotent (deleting twice has same effect as once)
     *
     * Returns 204 No Content on success
     * - Indicates successful deletion
     * - No response body needed
     *
     * In production, consider:
     * - Soft delete (mark as deleted, don't actually remove)
     * - Audit trail (who deleted, when)
     * - Cascade delete (related records)
     * - Authorization (who can delete)
     */
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete an order",
        description = "Deletes an order from the system. Use with caution in production."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Order deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteOrder(
            @Parameter(description = "Order ID", required = true, example = "507f1f77bcf86cd799439011")
            @PathVariable String id) {
        log.info("Received request to delete order: {}", id);
        
        orderService.deleteOrder(id);
        
        return ResponseEntity.noContent().build();  // HTTP 204 No Content
    }
    
    /**
     * EXCEPTION HANDLING:
     * 
     * Spring Boot provides automatic exception handling:
     * - RuntimeException -> 500 Internal Server Error
     * - Validation errors -> 400 Bad Request
     * 
     * For custom error handling, use @ControllerAdvice:
     * 
     * @ControllerAdvice
     * public class GlobalExceptionHandler {
     *     @ExceptionHandler(OrderNotFoundException.class)
     *     public ResponseEntity<ErrorResponse> handleOrderNotFound(OrderNotFoundException ex) {
     *         ErrorResponse error = new ErrorResponse(404, ex.getMessage());
     *         return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
     *     }
     * }
     */
    
    /**
     * CONTENT NEGOTIATION:
     * 
     * Spring Boot supports multiple response formats:
     * - JSON (default)
     * - XML (if jackson-dataformat-xml is on classpath)
     * 
     * Client specifies format using Accept header:
     * Accept: application/json -> Returns JSON
     * Accept: application/xml -> Returns XML
     * 
     * You can also use @Produces annotation:
     * @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
     */
    
    /**
     * VERSIONING:
     * 
     * API versioning strategies:
     * 
     * 1. URL versioning:
     *    @RequestMapping("/api/v1/orders")
     * 
     * 2. Header versioning:
     *    @GetMapping(headers = "X-API-VERSION=1")
     * 
     * 3. Media type versioning:
     *    @GetMapping(produces = "application/vnd.company.v1+json")
     */
}

