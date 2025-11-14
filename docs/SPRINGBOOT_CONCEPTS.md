# 🎓 SpringBoot Concepts Guide

## Complete Reference for Understanding This Project

This guide explains all SpringBoot concepts used in this project, perfect for interview preparation and understanding how to work with any SpringBoot application.

---

## Table of Contents

1. [SpringBoot Fundamentals](#springboot-fundamentals)
2. [Dependency Injection & IoC](#dependency-injection--ioc)
3. [Annotations Deep Dive](#annotations-deep-dive)
4. [Layered Architecture](#layered-architecture)
5. [Spring Data MongoDB](#spring-data-mongodb)
6. [REST API Development](#rest-api-development)
7. [Event-Driven Architecture](#event-driven-architecture)
8. [Spring Cloud Stream](#spring-cloud-stream)
9. [Configuration Management](#configuration-management)
10. [Testing in SpringBoot](#testing-in-springboot)

---

## 1. SpringBoot Fundamentals

### What is SpringBoot?

SpringBoot is an opinionated framework built on top of the Spring Framework that simplifies the development of production-ready applications.

**Key Principles:**
- **Convention over Configuration**: Sensible defaults reduce boilerplate
- **Auto-Configuration**: Automatically configures beans based on classpath
- **Standalone**: Embedded server (Tomcat, Jetty) - no WAR deployment needed
- **Production-Ready**: Built-in health checks, metrics, monitoring

### SpringBoot vs Express.js

| Feature | SpringBoot | Express.js |
|---------|-----------|------------|
| Language | Java | JavaScript/TypeScript |
| Type Safety | Strong, compile-time | Weak, runtime (unless TypeScript) |
| Dependency Injection | Built-in, automatic | Manual or libraries (InversifyJS) |
| Database ORM | Spring Data (JPA, MongoDB) | Sequelize, Mongoose, TypeORM |
| Configuration | application.yml/properties | .env files, config objects |
| Server | Embedded (Tomcat) | Explicit (app.listen()) |
| Async | CompletableFuture, Reactive | Promises, async/await |

### How SpringBoot Works

```
1. @SpringBootApplication annotation scans for components
2. Creates ApplicationContext (IoC Container)
3. Auto-configures beans based on dependencies
4. Injects dependencies where needed
5. Starts embedded web server
6. Application is ready to handle requests
```

**Example:**
```java
@SpringBootApplication  // Entry point
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
```

---

## 2. Dependency Injection & IoC

### Inversion of Control (IoC)

Instead of creating objects manually, Spring creates and manages them.

**Traditional Approach (Manual):**
```java
public class OrderController {
    private OrderService orderService = new OrderService();  // Tight coupling
}
```

**Spring Approach (IoC):**
```java
@RestController
public class OrderController {
    private final OrderService orderService;  // Spring injects this
    
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
}
```

### Dependency Injection Types

**1. Constructor Injection (Recommended)**
```java
@Service
@RequiredArgsConstructor  // Lombok generates constructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final EventPublisher eventPublisher;
}
```

**Benefits:**
- Immutable (thread-safe)
- Easy to test (pass mocks in constructor)
- Makes dependencies explicit
- Prevents NullPointerException

**2. Field Injection (Not Recommended)**
```java
@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;  // Avoid this!
}
```

**Why avoid?**
- Mutable (not thread-safe)
- Hard to test (need reflection to inject mocks)
- Hidden dependencies
- Can cause NullPointerException

### Bean Lifecycle

```
1. Spring scans for @Component, @Service, @Repository, @Controller
2. Creates bean instances (singleton by default)
3. Injects dependencies
4. Calls @PostConstruct methods (initialization)
5. Bean is ready to use
6. On shutdown, calls @PreDestroy methods (cleanup)
```

**Example:**
```java
@Service
public class OrderService {
    
    @PostConstruct
    public void init() {
        log.info("OrderService initialized");
    }
    
    @PreDestroy
    public void cleanup() {
        log.info("OrderService shutting down");
    }
}
```

---

## 3. Annotations Deep Dive

### Core Annotations

#### @SpringBootApplication
Combines three annotations:
```java
@Configuration      // Marks as configuration class
@EnableAutoConfiguration  // Enables auto-configuration
@ComponentScan      // Scans for components in package
```

#### @Component, @Service, @Repository, @Controller
All mark classes as Spring beans, but with semantic meaning:

```java
@Component  // Generic component
public class EmailSender { }

@Service    // Business logic layer
public class OrderService { }

@Repository // Data access layer (adds exception translation)
public interface OrderRepository extends MongoRepository<Order, String> { }

@Controller // Web layer (returns views)
public class WebController { }

@RestController  // REST API (returns JSON)
public class OrderController { }
```

### REST Annotations

```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @PostMapping  // POST /api/orders
    public OrderDTO create(@RequestBody OrderDTO order) { }
    
    @GetMapping("/{id}")  // GET /api/orders/123
    public OrderDTO getById(@PathVariable String id) { }
    
    @GetMapping  // GET /api/orders?status=PENDING
    public List<OrderDTO> getAll(@RequestParam String status) { }
    
    @PutMapping("/{id}")  // PUT /api/orders/123
    public OrderDTO update(@PathVariable String id, @RequestBody OrderDTO order) { }
    
    @DeleteMapping("/{id}")  // DELETE /api/orders/123
    public void delete(@PathVariable String id) { }
}
```

### Validation Annotations

```java
public class OrderDTO {
    @NotNull(message = "Customer ID is required")
    private String customerId;
    
    @NotBlank(message = "Name cannot be empty")
    private String customerName;
    
    @Positive(message = "Amount must be positive")
    private Double totalAmount;
    
    @Email(message = "Invalid email format")
    private String email;
    
    @Size(min = 1, max = 50, message = "Must have 1-50 items")
    private List<OrderItemDTO> items;
}
```

---

## 4. Layered Architecture

### Three-Tier Architecture

```
┌─────────────────────────────────────┐
│         Controller Layer            │  ← HTTP Requests/Responses
│  @RestController, @RequestMapping   │
└─────────────────────────────────────┘
                 ↓
┌─────────────────────────────────────┐
│          Service Layer              │  ← Business Logic
│  @Service, @Transactional           │
└─────────────────────────────────────┘
                 ↓
┌─────────────────────────────────────┐
│        Repository Layer             │  ← Data Access
│  @Repository, Spring Data           │
└─────────────────────────────────────┘
                 ↓
┌─────────────────────────────────────┐
│            Database                 │
│         MongoDB/MySQL               │
└─────────────────────────────────────┘
```

### Responsibilities

**Controller:**
- Handle HTTP requests
- Validate input
- Call service methods
- Return HTTP responses
- NO business logic

**Service:**
- Business logic
- Transaction management
- Coordinate multiple repositories
- Publish events
- NO HTTP concerns

**Repository:**
- Database queries
- CRUD operations
- NO business logic

### Example Flow

```java
// 1. Controller receives request
@PostMapping
public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderDTO orderDTO) {
    OrderDTO created = orderService.createOrder(orderDTO);  // Call service
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(created);
}

// 2. Service contains business logic
@Service
public class OrderService {
    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        Order order = mapToEntity(orderDTO);
        order.calculateTotalAmount();  // Business logic
        Order saved = orderRepository.save(order);  // Call repository
        eventPublisher.publishOrderCreated(mapToDTO(saved));  // Publish event
        return mapToDTO(saved);
    }
}

// 3. Repository handles database
@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByCustomerId(String customerId);
}
```

---

## 5. Spring Data MongoDB

### Repository Pattern

Spring Data MongoDB provides automatic implementation of repository interfaces.

```java
public interface OrderRepository extends MongoRepository<Order, String> {
    // No implementation needed! Spring generates it automatically
}
```

### Query Methods (Method Name Queries)

Spring generates queries from method names:

```java
// Find by single field
List<Order> findByCustomerId(String customerId);
// Generated: db.orders.find({ customerId: "..." })

// Find by multiple fields
List<Order> findByCustomerIdAndStatus(String customerId, OrderStatus status);
// Generated: db.orders.find({ customerId: "...", status: "..." })

// Find with comparison
List<Order> findByTotalAmountGreaterThan(Double amount);
// Generated: db.orders.find({ totalAmount: { $gt: amount } })

// Find with sorting
List<Order> findByCustomerIdOrderByCreatedAtDesc(String customerId);
// Generated: db.orders.find({ customerId: "..." }).sort({ createdAt: -1 })

// Find with date range
List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
// Generated: db.orders.find({ createdAt: { $gte: start, $lte: end } })
```

### Custom Queries

For complex queries, use @Query:

```java
@Query("{ 'totalAmount': { $gte: ?0, $lte: ?1 } }")
List<Order> findByTotalAmountBetween(Double min, Double max);

@Query("{ 'items.productId': ?0 }")
List<Order> findByProductId(String productId);
```

### Entity Mapping

```java
@Document(collection = "orders")  // MongoDB collection name
public class Order {
    
    @Id  // Primary key
    private String id;
    
    private String customerId;
    
    @Indexed  // Create index for faster queries
    private OrderStatus status;
    
    @DBRef  // Reference to another collection
    private Customer customer;
    
    // Embedded document (not separate collection)
    private List<OrderItem> items;
}
```

---

## 6. REST API Development

### HTTP Methods & Status Codes

```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    // CREATE - POST - 201 Created
    @PostMapping
    public ResponseEntity<OrderDTO> create(@RequestBody OrderDTO order) {
        OrderDTO created = service.create(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    // READ - GET - 200 OK
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getById(@PathVariable String id) {
        OrderDTO order = service.getById(id);
        return ResponseEntity.ok(order);
    }
    
    // UPDATE - PUT - 200 OK
    @PutMapping("/{id}")
    public ResponseEntity<OrderDTO> update(@PathVariable String id, @RequestBody OrderDTO order) {
        OrderDTO updated = service.update(id, order);
        return ResponseEntity.ok(updated);
    }
    
    // DELETE - DELETE - 204 No Content
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

### Request/Response Handling

**Path Variables:**
```java
@GetMapping("/orders/{id}/items/{itemId}")
public OrderItem getItem(@PathVariable String id, @PathVariable String itemId) {
    // URL: /orders/123/items/456
    // id = "123", itemId = "456"
}
```

**Query Parameters:**
```java
@GetMapping("/orders")
public List<Order> search(
    @RequestParam(required = false) String status,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
) {
    // URL: /orders?status=PENDING&page=0&size=20
}
```

**Request Body:**
```java
@PostMapping("/orders")
public Order create(@Valid @RequestBody OrderDTO orderDTO) {
    // Spring automatically deserializes JSON to OrderDTO
    // @Valid triggers validation
}
```

### Exception Handling

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(OrderNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(404, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.toList());
        ErrorResponse error = new ErrorResponse(400, "Validation failed", errors);
        return ResponseEntity.badRequest().body(error);
    }
}
```

---

## 7. Event-Driven Architecture

### Core Concepts

**Event:** Something that has happened (past tense)
- OrderCreated, InventoryReserved, PaymentProcessed

**Event Producer:** Publishes events
**Event Consumer:** Subscribes to and processes events
**Message Broker:** Routes events (RabbitMQ, Kafka)

### Benefits

1. **Loose Coupling**: Services don't know about each other
2. **Scalability**: Add consumers without changing producers
3. **Resilience**: If one service fails, others continue
4. **Audit Trail**: All events are logged
5. **Temporal Decoupling**: Producer and consumer don't need to be online simultaneously

### Saga Pattern

Coordinates distributed transactions using events:

```
Order Service:
1. Create order (PENDING)
2. Publish ORDER_CREATED event
3. Publish INVENTORY_CHECK_REQUESTED event

Inventory Service:
4. Receive INVENTORY_CHECK_REQUESTED
5. Check stock
6a. If available: Reserve stock, publish INVENTORY_RESERVED
6b. If not available: Publish INVENTORY_INSUFFICIENT

Order Service:
7a. Receive INVENTORY_RESERVED → Confirm order
7b. Receive INVENTORY_INSUFFICIENT → Cancel order (compensation)
```

### Event Sourcing

Store all state changes as events:

```java
// Traditional: Store current state
Order order = { id: "123", status: "CONFIRMED", total: 99.99 }

// Event Sourcing: Store all events
OrderCreatedEvent { orderId: "123", total: 99.99, timestamp: "..." }
InventoryReservedEvent { orderId: "123", timestamp: "..." }
OrderConfirmedEvent { orderId: "123", timestamp: "..." }

// Reconstruct current state by replaying events
```

**Benefits:**
- Complete audit trail
- Time travel (replay to any point)
- Debug and troubleshoot easily
- Add new features by replaying old events

---

## 8. Spring Cloud Stream

### Overview

Spring Cloud Stream abstracts message brokers (RabbitMQ, Kafka) with a consistent API.

### Functional Programming Model

```java
@Configuration
public class EventListeners {
    
    // Consumer: Receives messages
    @Bean
    public Consumer<OrderEvent> orderEventConsumer() {
        return event -> {
            log.info("Received: {}", event);
            processEvent(event);
        };
    }
    
    // Function: Transforms messages
    @Bean
    public Function<OrderEvent, NotificationEvent> orderToNotification() {
        return orderEvent -> {
            return NotificationEvent.from(orderEvent);
        };
    }
    
    // Supplier: Produces messages
    @Bean
    public Supplier<OrderEvent> orderEventSupplier() {
        return () -> {
            return generateEvent();
        };
    }
}
```

### Publishing Events

```java
@Component
@RequiredArgsConstructor
public class EventPublisher {
    private final StreamBridge streamBridge;
    
    public void publishOrderCreated(OrderDTO order) {
        OrderEvent event = OrderEvent.create(
            OrderEventType.ORDER_CREATED,
            order,
            "order-service"
        );
        streamBridge.send("orderEvents-out-0", event);
    }
}
```

### Configuration

```yaml
spring:
  cloud:
    function:
      definition: orderEventConsumer  # Bean name
    stream:
      bindings:
        orderEvents-out-0:  # Output binding
          destination: order.events  # RabbitMQ exchange
        orderEventConsumer-in-0:  # Input binding
          destination: order.events
          group: order-service  # Consumer group
```

### Consumer Groups

Multiple instances share work:

```
Instance 1 (group: order-service) ─┐
Instance 2 (group: order-service) ─┼─→ RabbitMQ delivers to ONE instance
Instance 3 (group: order-service) ─┘
```

This enables horizontal scaling!

---

## 9. Configuration Management

### application.yml Structure

```yaml
# Server configuration
server:
  port: 8081

# Spring configuration
spring:
  application:
    name: order-service
  data:
    mongodb:
      uri: mongodb://localhost:27017/order-db

# Custom properties
app:
  order:
    max-items: 50
```

### Environment-Specific Configuration

```yaml
# application.yml (default)
spring:
  profiles:
    active: dev

---
# application-dev.yml
spring:
  config:
    activate:
      on-profile: dev
  data:
    mongodb:
      uri: mongodb://localhost:27017/order-db-dev

---
# application-prod.yml
spring:
  config:
    activate:
      on-profile: prod
  data:
    mongodb:
      uri: ${MONGODB_URI}  # Environment variable
```

### Using Configuration Properties

```java
@Configuration
@ConfigurationProperties(prefix = "app.order")
@Data
public class OrderProperties {
    private int maxItems;
    private double maxTotalAmount;
}

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderProperties properties;
    
    public void validateOrder(Order order) {
        if (order.getItems().size() > properties.getMaxItems()) {
            throw new ValidationException("Too many items");
        }
    }
}
```

---

## 10. Testing in SpringBoot

### Unit Testing

```java
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    
    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private EventPublisher eventPublisher;
    
    @InjectMocks
    private OrderService orderService;
    
    @Test
    void createOrder_ShouldSaveAndPublishEvent() {
        // Arrange
        OrderDTO orderDTO = OrderDTO.builder()
            .customerId("CUST123")
            .build();
        Order order = new Order();
        when(orderRepository.save(any())).thenReturn(order);
        
        // Act
        OrderDTO result = orderService.createOrder(orderDTO);
        
        // Assert
        assertNotNull(result);
        verify(orderRepository).save(any());
        verify(eventPublisher).publishOrderCreated(any());
    }
}
```

### Integration Testing

```java
@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void createOrder_ShouldReturn202() throws Exception {
        OrderDTO orderDTO = OrderDTO.builder()
            .customerId("CUST123")
            .customerName("John Doe")
            .build();
        
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderDTO)))
            .andExpect(status().isAccepted())
            .andExpect(jsonPath("$.orderId").exists());
    }
}
```

---

## Key Takeaways for Interviews

### 1. SpringBoot Advantages
- Auto-configuration reduces boilerplate
- Embedded server (no deployment complexity)
- Production-ready features (health checks, metrics)
- Strong ecosystem (Spring Data, Spring Cloud, Spring Security)

### 2. Dependency Injection Benefits
- Loose coupling (easy to swap implementations)
- Testability (inject mocks)
- Centralized configuration
- Lifecycle management

### 3. Event-Driven Architecture
- Microservices communicate via events
- Saga pattern for distributed transactions
- Eventual consistency
- Scalability and resilience

### 4. Best Practices
- Use constructor injection
- Separate DTOs from entities
- Follow layered architecture
- Handle exceptions globally
- Use profiles for different environments
- Write tests (unit + integration)

### 5. Common Interview Questions

**Q: What is the difference between @Component, @Service, and @Repository?**
A: All mark classes as Spring beans, but with semantic meaning. @Service for business logic, @Repository for data access (adds exception translation), @Component for generic components.

**Q: How does Spring Boot auto-configuration work?**
A: Spring Boot scans the classpath for dependencies and automatically configures beans. For example, if spring-boot-starter-web is present, it configures Tomcat and DispatcherServlet.

**Q: What is the Saga pattern?**
A: A pattern for managing distributed transactions using events. Each service performs its local transaction and publishes events. If a step fails, compensation events are published to undo previous steps.

**Q: Why use DTOs instead of entities in REST APIs?**
A: DTOs decouple the API from the database schema, provide security (don't expose internal fields), enable versioning, and allow different API structures than database structures.

**Q: How do you handle errors in event-driven systems?**
A: Use retry mechanisms, dead letter queues (DLQ), idempotent event handlers, and compensation events (Saga pattern).

---

## Next Steps

1. **Practice**: Build small projects using these concepts
2. **Read**: Spring Boot documentation and source code
3. **Debug**: Use breakpoints to understand the flow
4. **Experiment**: Try different configurations and patterns
5. **Interview**: Explain concepts in your own words

Remember: Understanding WHY is more important than memorizing HOW!