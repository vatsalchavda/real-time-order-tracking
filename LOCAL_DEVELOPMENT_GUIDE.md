# Local Development Guide - Ubuntu Setup

## 🎯 Overview

This guide will help you set up and run the entire Real-Time Order Tracking System on your local Ubuntu machine. Perfect for development, testing, and interview demonstrations.

**What You'll Learn:**
- Complete Ubuntu environment setup
- Running all services locally with Docker
- Testing the full event-driven workflow
- Debugging and troubleshooting
- Adding new endpoints and features

---

## 📋 Prerequisites

### System Requirements
- **OS:** Ubuntu 20.04 LTS or later (or WSL2 on Windows)
- **RAM:** Minimum 8GB (16GB recommended)
- **Disk:** 20GB free space
- **CPU:** 4 cores recommended

### Required Software

#### 1. Java Development Kit (JDK 17)
```bash
# Install OpenJDK 17
sudo apt update
sudo apt install -y openjdk-17-jdk

# Verify installation
java -version
# Should show: openjdk version "17.x.x"

# Set JAVA_HOME
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> ~/.bashrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
source ~/.bashrc
```

#### 2. Maven (Build Tool)
```bash
# Install Maven
sudo apt install -y maven

# Verify installation
mvn -version
# Should show: Apache Maven 3.x.x
```

#### 3. Node.js and npm (Frontend)
```bash
# Install Node.js 18.x LTS
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt install -y nodejs

# Verify installation
node --version  # Should show: v18.x.x
npm --version   # Should show: 9.x.x or higher
```

#### 4. Docker and Docker Compose (Containers)
```bash
# Install Docker
sudo apt update
sudo apt install -y apt-transport-https ca-certificates curl software-properties-common
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin

# Add your user to docker group (avoid using sudo)
sudo usermod -aG docker $USER
newgrp docker

# Verify installation
docker --version
docker compose version
```

#### 5. Git (Version Control)
```bash
# Install Git
sudo apt install -y git

# Configure Git
git config --global user.name "Vatsal Chavda"
git config --global user.email "vatsalchavda2@gmail.com"

# Verify
git config --list
```

#### 6. VS Code (IDE)
```bash
# Download and install VS Code
wget -qO- https://packages.microsoft.com/keys/microsoft.asc | gpg --dearmor > packages.microsoft.gpg
sudo install -o root -g root -m 644 packages.microsoft.gpg /etc/apt/trusted.gpg.d/
sudo sh -c 'echo "deb [arch=amd64,arm64,armhf signed-by=/etc/apt/trusted.gpg.d/packages.microsoft.gpg] https://packages.microsoft.com/repos/code stable main" > /etc/apt/sources.list.d/vscode.list'
sudo apt update
sudo apt install -y code

# Launch VS Code
code .
```

**Recommended VS Code Extensions:**
- Extension Pack for Java (Microsoft)
- Spring Boot Extension Pack (VMware)
- Docker (Microsoft)
- ESLint (for React)
- Prettier (code formatter)
- Thunder Client (API testing)

#### 7. IntelliJ IDEA Community Edition (Alternative IDE)
```bash
# Download IntelliJ IDEA
sudo snap install intellij-idea-community --classic

# Launch
intellij-idea-community
```

---

## 🚀 Quick Start (5 Minutes)

### Step 1: Clone the Repository
```bash
cd ~
git clone https://github.com/vatsalchavda/real-time-order-tracking.git
cd real-time-order-tracking
```

### Step 2: Start Infrastructure Services
```bash
# Start MongoDB and RabbitMQ using Docker Compose
docker compose up -d mongodb rabbitmq

# Verify services are running
docker compose ps

# Check logs
docker compose logs -f mongodb
docker compose logs -f rabbitmq
```

**Access Points:**
- MongoDB: `mongodb://localhost:27017`
- RabbitMQ Management UI: `http://localhost:15672` (guest/guest)

### Step 3: Build All Services
```bash
# Make build script executable
chmod +x build.sh

# Build all backend services
./build.sh

# This will:
# 1. Build common module
# 2. Build order-service
# 3. Build inventory-service
# 4. Build notification-service
# 5. Build api-gateway
```

### Step 4: Start All Services
```bash
# Make start script executable
chmod +x start-dev.sh

# Start all services
./start-dev.sh

# Services will start on:
# - Order Service: http://localhost:8081
# - Inventory Service: http://localhost:8082
# - Notification Service: http://localhost:8083
# - API Gateway: http://localhost:8080
```

### Step 5: Start Frontend
```bash
# Open new terminal
cd frontend

# Install dependencies (first time only)
npm install

# Start React development server
npm start

# Frontend will open at: http://localhost:3000
```

### Step 6: Test the System
```bash
# Open browser to http://localhost:3000
# Create a new order
# Watch the order flow through all services
# Check RabbitMQ UI to see messages
```

---

## 📁 Project Structure

```
real-time-order-tracking/
├── backend/
│   ├── common/                    # Shared DTOs, Events, Enums
│   ├── order-service/             # Order management (Port 8081)
│   ├── inventory-service/         # Inventory management (Port 8082)
│   ├── notification-service/      # WebSocket notifications (Port 8083)
│   ├── api-gateway/               # API Gateway (Port 8080)
│   └── pom.xml                    # Parent POM
├── frontend/                      # React application
├── docker-compose.yml             # Infrastructure services
├── build.sh                       # Build all services
├── start-dev.sh                   # Start all services
├── stop-dev.sh                    # Stop all services
├── test.sh                        # Run tests
└── LOCAL_DEVELOPMENT_GUIDE.md     # This file
```

---

## 🔧 Detailed Setup Instructions

### Building Individual Services

#### Build Common Module (Required First)
```bash
cd backend/common
mvn clean install
```

#### Build Order Service
```bash
cd backend/order-service
mvn clean package

# Run standalone
java -jar target/order-service-1.0.0.jar
```

#### Build Inventory Service
```bash
cd backend/inventory-service
mvn clean package

# Run standalone
java -jar target/inventory-service-1.0.0.jar
```

#### Build Notification Service
```bash
cd backend/notification-service
mvn clean package

# Run standalone
java -jar target/notification-service-1.0.0.jar
```

#### Build API Gateway
```bash
cd backend/api-gateway
mvn clean package

# Run standalone
java -jar target/api-gateway-1.0.0.jar
```

### Running Services in Development Mode

#### Option 1: Using Maven (Hot Reload)
```bash
# Terminal 1: Order Service
cd backend/order-service
mvn spring-boot:run

# Terminal 2: Inventory Service
cd backend/inventory-service
mvn spring-boot:run

# Terminal 3: Notification Service
cd backend/notification-service
mvn spring-boot:run

# Terminal 4: API Gateway
cd backend/api-gateway
mvn spring-boot:run

# Terminal 5: Frontend
cd frontend
npm start
```

#### Option 2: Using JAR Files
```bash
# Build all first
./build.sh

# Then run each service
java -jar backend/order-service/target/order-service-1.0.0.jar &
java -jar backend/inventory-service/target/inventory-service-1.0.0.jar &
java -jar backend/notification-service/target/notification-service-1.0.0.jar &
java -jar backend/api-gateway/target/api-gateway-1.0.0.jar &
```

#### Option 3: Using Docker Compose (Full Stack)
```bash
# Build and start everything
docker compose up --build

# Stop everything
docker compose down
```

---

## 🧪 Testing the System

### 1. Health Checks
```bash
# Check Order Service
curl http://localhost:8081/actuator/health

# Check Inventory Service
curl http://localhost:8082/actuator/health

# Check Notification Service
curl http://localhost:8083/actuator/health

# Check API Gateway
curl http://localhost:8080/actuator/health
```

### 2. API Testing with curl

#### Create an Order
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Vatsal Chavda",
    "customerEmail": "vatsal@example.com",
    "shippingAddress": "123 Main St, Ottawa, ON",
    "items": [
      {
        "productId": "PROD-001",
        "productName": "Laptop",
        "quantity": 1,
        "price": 1299.99
      }
    ]
  }'
```

#### Get All Orders
```bash
curl http://localhost:8080/api/orders
```

#### Get Order by ID
```bash
curl http://localhost:8080/api/orders/{orderId}
```

#### Update Order Status
```bash
curl -X PATCH http://localhost:8080/api/orders/{orderId}/status?status=CONFIRMED
```

#### Delete Order
```bash
curl -X DELETE http://localhost:8080/api/orders/{orderId}
```

### 3. Testing with Thunder Client (VS Code)

1. Install Thunder Client extension in VS Code
2. Create a new collection "Order Management"
3. Add requests for each endpoint
4. Save and organize your tests

### 4. Monitoring RabbitMQ Messages

1. Open RabbitMQ Management UI: `http://localhost:15672`
2. Login: guest/guest
3. Go to "Queues" tab
4. Watch messages flow through:
   - `order.created` queue
   - `order.confirmed` queue
   - `inventory.reserved` queue
   - `notification.sent` queue

### 5. Monitoring MongoDB

```bash
# Connect to MongoDB
docker exec -it mongodb mongosh

# Switch to orderdb
use orderdb

# View all orders
db.orders.find().pretty()

# Count orders
db.orders.countDocuments()

# Find orders by status
db.orders.find({status: "PENDING"}).pretty()

# Exit
exit
```

---

## 🎯 Interview Demo Workflow

### Scenario: Complete Order Processing Flow

**Step 1: Start All Services**
```bash
# Terminal 1: Infrastructure
docker compose up -d mongodb rabbitmq

# Terminal 2: Backend Services
./start-dev.sh

# Terminal 3: Frontend
cd frontend && npm start
```

**Step 2: Create Order (Frontend)**
1. Open `http://localhost:3000`
2. Fill in order form
3. Click "Create Order"
4. Show order appears with PENDING status

**Step 3: Show Event Flow**
1. Open RabbitMQ UI: `http://localhost:15672`
2. Show `order.created` event in queue
3. Explain: "Order Service published event to RabbitMQ"

**Step 4: Show Inventory Processing**
1. Check Inventory Service logs
2. Show: "Inventory Service consumed event and reserved stock"
3. Show `inventory.reserved` event published

**Step 5: Show Order Status Update**
1. Refresh frontend
2. Show order status changed to CONFIRMED
3. Explain: "Order Service consumed inventory.reserved event"

**Step 6: Show Notification**
1. Check Notification Service logs
2. Show: "Notification sent via WebSocket"
3. Explain: "Real-time notification to user"

**Step 7: Show MongoDB Data**
```bash
docker exec -it mongodb mongosh
use orderdb
db.orders.find().pretty()
```

**Step 8: Demonstrate Adding New Endpoint**
```bash
# Show how to add a new endpoint in OrderController
code backend/order-service/src/main/java/com/eventdriven/oms/orderservice/controller/OrderController.java

# Add new endpoint (example):
@GetMapping("/search")
public ResponseEntity<List<Order>> searchOrders(@RequestParam String customerName) {
    // Implementation
}

# Rebuild and test
cd backend/order-service
mvn clean package
java -jar target/order-service-1.0.0.jar
```

---

## 🐛 Debugging and Troubleshooting

### Common Issues

#### Issue 1: Port Already in Use
```bash
# Find process using port
sudo lsof -i :8081

# Kill process
kill -9 <PID>

# Or change port in application.properties
server.port=8091
```

#### Issue 2: MongoDB Connection Failed
```bash
# Check if MongoDB is running
docker compose ps

# Restart MongoDB
docker compose restart mongodb

# Check logs
docker compose logs mongodb
```

#### Issue 3: RabbitMQ Connection Failed
```bash
# Check if RabbitMQ is running
docker compose ps

# Restart RabbitMQ
docker compose restart rabbitmq

# Check logs
docker compose logs rabbitmq
```

#### Issue 4: Maven Build Failed
```bash
# Clean Maven cache
mvn clean

# Delete .m2 repository (nuclear option)
rm -rf ~/.m2/repository

# Rebuild
mvn clean install
```

#### Issue 5: Frontend Not Starting
```bash
# Clear npm cache
npm cache clean --force

# Delete node_modules
rm -rf node_modules package-lock.json

# Reinstall
npm install

# Start
npm start
```

### Debugging in VS Code

#### Debug Order Service
1. Open `backend/order-service` in VS Code
2. Press F5 or click "Run and Debug"
3. Select "Spring Boot" configuration
4. Set breakpoints in code
5. Make API request
6. Step through code

#### Debug Frontend
1. Open `frontend` in VS Code
2. Press F5 or click "Run and Debug"
3. Select "Chrome" configuration
4. Set breakpoints in React components
5. Interact with UI
6. Step through code

### Viewing Logs

#### Service Logs
```bash
# Order Service logs
tail -f backend/order-service/logs/application.log

# Or if running with Maven
cd backend/order-service
mvn spring-boot:run
# Logs appear in terminal
```

#### Docker Logs
```bash
# All services
docker compose logs -f

# Specific service
docker compose logs -f order-service

# Last 100 lines
docker compose logs --tail=100 order-service
```

---

## 📚 Learning Path for Interview

### Day 1: Setup and Basics (Today)
- [ ] Complete Ubuntu setup
- [ ] Install all prerequisites
- [ ] Clone and build project
- [ ] Run all services locally
- [ ] Test basic order creation
- [ ] Read SPRINGBOOT_CONCEPTS.md
- [ ] Understand project structure

### Day 2: Deep Dive
- [ ] Study OrderController.java - understand REST endpoints
- [ ] Study OrderService.java - understand business logic
- [ ] Study OrderEventPublisher.java - understand event publishing
- [ ] Study InventoryEventListener.java - understand event consumption
- [ ] Add a new GET endpoint for order search
- [ ] Test the new endpoint
- [ ] Read Event-Driven Architecture guide

### Day 3: Interview Preparation
- [ ] Practice demo workflow (10 times)
- [ ] Prepare answers for common questions
- [ ] Review Swagger documentation
- [ ] Practice explaining architecture
- [ ] Practice adding new features live
- [ ] Review troubleshooting scenarios
- [ ] Mock interview with friend

---

## 🎓 Key Concepts to Master

### 1. SpringBoot Annotations
```java
@SpringBootApplication  // Main application class
@RestController        // REST API controller
@Service              // Business logic layer
@Repository           // Data access layer
@Autowired            // Dependency injection
@GetMapping           // HTTP GET endpoint
@PostMapping          // HTTP POST endpoint
@RequestBody          // Parse JSON request
@PathVariable         // URL path parameter
@RequestParam         // Query parameter
```

### 2. Event-Driven Architecture
- **Event Publishing:** OrderEventPublisher uses RabbitTemplate
- **Event Consumption:** @RabbitListener annotation
- **Message Broker:** RabbitMQ for async communication
- **Event Types:** OrderCreated, OrderConfirmed, InventoryReserved
- **Benefits:** Loose coupling, scalability, resilience

### 3. MongoDB Operations
```java
@Document(collection = "orders")  // MongoDB collection
@Id                               // Primary key
@Indexed                          // Create index
orderRepository.save()            // Insert/Update
orderRepository.findById()        // Find by ID
orderRepository.findAll()         // Find all
orderRepository.deleteById()      // Delete
```

### 4. REST API Design
- **GET /api/orders** - List all orders
- **GET /api/orders/{id}** - Get order by ID
- **POST /api/orders** - Create new order
- **PATCH /api/orders/{id}/status** - Update status
- **DELETE /api/orders/{id}** - Delete order

### 5. Docker Concepts
- **Container:** Isolated runtime environment
- **Image:** Template for containers
- **docker-compose.yml:** Multi-container orchestration
- **Volumes:** Persistent data storage
- **Networks:** Container communication

---

## 🚀 Adding New Features (Live Demo)

### Example: Add Order Search Endpoint

#### Step 1: Add Repository Method
```java
// File: OrderRepository.java
public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByCustomerNameContainingIgnoreCase(String customerName);
    List<Order> findByStatus(OrderStatus status);
}
```

#### Step 2: Add Service Method
```java
// File: OrderService.java
public List<Order> searchOrders(String customerName) {
    return orderRepository.findByCustomerNameContainingIgnoreCase(customerName);
}

public List<Order> getOrdersByStatus(OrderStatus status) {
    return orderRepository.findByStatus(status);
}
```

#### Step 3: Add Controller Endpoint
```java
// File: OrderController.java
@GetMapping("/search")
public ResponseEntity<List<Order>> searchOrders(
    @RequestParam String customerName) {
    List<Order> orders = orderService.searchOrders(customerName);
    return ResponseEntity.ok(orders);
}

@GetMapping("/status/{status}")
public ResponseEntity<List<Order>> getOrdersByStatus(
    @PathVariable OrderStatus status) {
    List<Order> orders = orderService.getOrdersByStatus(status);
    return ResponseEntity.ok(orders);
}
```

#### Step 4: Test
```bash
# Rebuild
cd backend/order-service
mvn clean package

# Restart service
java -jar target/order-service-1.0.0.jar

# Test new endpoint
curl "http://localhost:8081/api/orders/search?customerName=Vatsal"
curl "http://localhost:8081/api/orders/status/PENDING"
```

---

## 📊 Swagger API Documentation

Access Swagger UI at: `http://localhost:8081/swagger-ui.html`

**Features:**
- Interactive API documentation
- Test endpoints directly from browser
- View request/response schemas
- See all available endpoints

---

## 🎯 Interview Questions You Should Be Ready For

### Architecture Questions
1. **Q:** Explain the architecture of this system.
   **A:** Microservices architecture with event-driven communication. Order Service handles orders, Inventory Service manages stock, Notification Service sends alerts. They communicate asynchronously via RabbitMQ.

2. **Q:** Why use event-driven architecture?
   **A:** Loose coupling, scalability, resilience. Services can be deployed independently. If one service is down, events are queued and processed when it's back up.

3. **Q:** How do you handle distributed transactions?
   **A:** Using Saga pattern. Each service performs local transaction and publishes event. If something fails, compensating transactions are triggered.

### SpringBoot Questions
1. **Q:** What is @SpringBootApplication?
   **A:** Combination of @Configuration, @EnableAutoConfiguration, and @ComponentScan. It bootstraps the Spring application.

2. **Q:** Explain dependency injection.
   **A:** Spring manages object creation and dependencies. Use @Autowired to inject dependencies. Promotes loose coupling and testability.

3. **Q:** What is the difference between @RestController and @Controller?
   **A:** @RestController = @Controller + @ResponseBody. It automatically serializes return values to JSON.

### MongoDB Questions
1. **Q:** Why MongoDB for this project?
   **A:** Document-based, flexible schema, good for order data with varying structures. Easy to scale horizontally.

2. **Q:** How do you handle relationships in MongoDB?
   **A:** Embedding (order items inside order) or referencing (separate collections with IDs). We use embedding for order items.

### RabbitMQ Questions
1. **Q:** What is RabbitMQ?
   **A:** Message broker for asynchronous communication. Implements AMQP protocol. Ensures reliable message delivery.

2. **Q:** What are exchanges and queues?
   **A:** Exchange routes messages to queues based on routing keys. Queues store messages until consumers process them.

---

## 🔗 Useful Commands Cheat Sheet

### Docker Commands
```bash
docker compose up -d              # Start all services
docker compose down               # Stop all services
docker compose ps                 # List running services
docker compose logs -f service    # View logs
docker compose restart service    # Restart service
docker exec -it container bash    # Enter container
```

### Maven Commands
```bash
mvn clean                         # Clean build artifacts
mvn compile                       # Compile source code
mvn test                          # Run tests
mvn package                       # Create JAR file
mvn install                       # Install to local repo
mvn spring-boot:run              # Run Spring Boot app
```

### Git Commands
```bash
git status                        # Check status
git add .                         # Stage all changes
git commit -m "message"          # Commit changes
git push origin main             # Push to GitHub
git pull origin main             # Pull latest changes
git log --oneline                # View commit history
```

### MongoDB Commands
```bash
mongosh                          # Connect to MongoDB
use orderdb                      # Switch database
db.orders.find()                 # Find all documents
db.orders.findOne()              # Find one document
db.orders.countDocuments()       # Count documents
db.orders.deleteMany({})         # Delete all documents
```

---

## 📞 Getting Help

### Documentation
- SpringBoot: https://spring.io/projects/spring-boot
- MongoDB: https://docs.mongodb.com/
- RabbitMQ: https://www.rabbitmq.com/documentation.html
- React: https://react.dev/

### Troubleshooting
1. Check service logs
2. Verify all services are running
3. Check MongoDB connection
4. Check RabbitMQ connection
5. Review application.properties
6. Check port conflicts

---

## ✅ Pre-Interview Checklist

- [ ] All services start without errors
- [ ] Can create orders via UI
- [ ] Can create orders via API
- [ ] Events flow through RabbitMQ
- [ ] Order status updates correctly
- [ ] Can view orders in MongoDB
- [ ] Swagger documentation works
- [ ] Can add new endpoint live
- [ ] Can explain architecture clearly
- [ ] Can troubleshoot common issues
- [ ] Practiced demo 10+ times

---

## 🎉 You're Ready!

You now have a complete local development environment for your interview. Practice the demo workflow multiple times, understand the code deeply, and be ready to add features live during the interview.

**Good luck with your interview at Solace!** 🚀

---

**Author:** Vatsal Chavda  
**Email:** vatsalchavda2@gmail.com  
**GitHub:** https://github.com/vatsalchavda/real-time-order-tracking