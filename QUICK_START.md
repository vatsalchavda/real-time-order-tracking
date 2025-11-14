# 🚀 Quick Start Guide

## Get This Project Running in 15 Minutes

This guide will help you get the project up and running quickly for your interview preparation.

---

## Prerequisites Check

Before starting, ensure you have:

```bash
# Check Java version (need 17+)
java -version

# Check Maven version (need 3.8+)
mvn -version

# Check Docker
docker --version
docker-compose --version

# Check Node.js (need 18+)
node --version
npm --version
```

If any are missing, install them first.

---

## Option 1: Docker Compose (Recommended - 5 minutes)

### Step 1: Clone and Navigate
```bash
git clone <your-repo-url>
cd real-time-order-tracking
```

### Step 2: Start Everything
```bash
docker-compose up -d
```

### Step 3: Wait for Services to Start (2-3 minutes)
```bash
# Check if services are running
docker-compose ps

# View logs
docker-compose logs -f
```

### Step 4: Access the Application
- **Frontend**: http://localhost:3000
- **Order Service API**: http://localhost:8081/api/orders
- **RabbitMQ Management**: http://localhost:15672 (guest/guest)
- **Health Check**: http://localhost:8081/actuator/health

### Step 5: Test the API
```bash
# Create an order
curl -X POST http://localhost:8081/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST123",
    "customerName": "John Doe",
    "items": [
      {
        "productId": "PROD001",
        "productName": "Laptop",
        "quantity": 1,
        "price": 999.99
      }
    ],
    "shippingAddress": "123 Main St"
  }'

# Get all orders
curl http://localhost:8081/api/orders
```

---

## Option 2: Manual Setup (15 minutes)

### Step 1: Start Infrastructure

```bash
# Start MongoDB
docker run -d -p 27017:27017 --name mongodb mongo:7.0

# Start RabbitMQ
docker run -d -p 5672:5672 -p 15672:15672 --name rabbitmq rabbitmq:3.12-management

# Wait 30 seconds for services to start
```

### Step 2: Build Backend

```bash
cd backend

# Build all modules (this will take 2-3 minutes first time)
mvn clean install -DskipTests

# If build fails, try:
mvn clean install -DskipTests -U
```

### Step 3: Run Order Service

```bash
cd order-service
mvn spring-boot:run

# Wait for "Started OrderServiceApplication" message
# Keep this terminal open
```

### Step 4: Run Inventory Service (New Terminal)

```bash
cd backend/inventory-service
mvn spring-boot:run

# Wait for "Started InventoryServiceApplication" message
# Keep this terminal open
```

### Step 5: Test the Services

```bash
# In a new terminal
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health

# Both should return {"status":"UP"}
```

---

## Troubleshooting

### Issue: Port Already in Use

```bash
# Find process using port 8081
lsof -i :8081

# Kill the process
kill -9 <PID>

# Or use different ports in application.yml
```

### Issue: MongoDB Connection Failed

```bash
# Check if MongoDB is running
docker ps | grep mongodb

# Restart MongoDB
docker restart mongodb

# Check logs
docker logs mongodb
```

### Issue: RabbitMQ Connection Failed

```bash
# Check if RabbitMQ is running
docker ps | grep rabbitmq

# Restart RabbitMQ
docker restart rabbitmq

# Access management UI
open http://localhost:15672
```

### Issue: Maven Build Fails

```bash
# Clear Maven cache
rm -rf ~/.m2/repository

# Rebuild
mvn clean install -U -DskipTests
```

### Issue: "Cannot find symbol" Errors

```bash
# Lombok not working - install Lombok plugin in your IDE
# IntelliJ: Settings → Plugins → Search "Lombok" → Install
# Eclipse: Download lombok.jar and run it

# Enable annotation processing
# IntelliJ: Settings → Build → Compiler → Annotation Processors → Enable
```

---

## Verify Everything Works

### 1. Check Services are Running

```bash
# Order Service
curl http://localhost:8081/actuator/health

# Inventory Service  
curl http://localhost:8082/actuator/health

# RabbitMQ
open http://localhost:15672
```

### 2. Create a Test Order

```bash
curl -X POST http://localhost:8081/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "TEST123",
    "customerName": "Test User",
    "items": [
      {
        "productId": "PROD001",
        "productName": "Test Product",
        "quantity": 1,
        "price": 99.99
      }
    ],
    "shippingAddress": "123 Test St"
  }'
```

### 3. Check RabbitMQ for Events

1. Open http://localhost:15672
2. Login with guest/guest
3. Go to "Queues" tab
4. You should see queues with messages

### 4. Check MongoDB for Data

```bash
# Connect to MongoDB
docker exec -it mongodb mongosh

# Switch to order-db
use order-db

# View orders
db.orders.find().pretty()

# Exit
exit
```

---

## Next Steps

### For Interview Preparation

1. **Read the Documentation**
   - [SpringBoot Concepts Guide](docs/SPRINGBOOT_CONCEPTS.md)
   - [Interview Walkthrough](docs/INTERVIEW_WALKTHROUGH.md)

2. **Understand the Code Flow**
   - Start with `OrderController.java`
   - Follow the flow: Controller → Service → Repository
   - Understand event publishing and consuming

3. **Practice Explaining**
   - Architecture diagram
   - Event flow (Saga pattern)
   - Why you chose each technology
   - Trade-offs and decisions

4. **Prepare Demo**
   - Have the application running
   - Know how to create an order via API
   - Show RabbitMQ management UI
   - Explain the event flow

### For Development

1. **Add More Features**
   - Payment service
   - Shipping service
   - User authentication
   - Order cancellation

2. **Improve Code**
   - Add more tests
   - Implement caching
   - Add API documentation (Swagger)
   - Implement circuit breakers

3. **Deploy**
   - Deploy to cloud (AWS, Azure, GCP)
   - Set up CI/CD pipeline
   - Add monitoring (Prometheus, Grafana)
   - Implement logging (ELK stack)

---

## Common Commands

### Docker Compose

```bash
# Start all services
docker-compose up -d

# Stop all services
docker-compose down

# View logs
docker-compose logs -f

# Restart a service
docker-compose restart order-service

# Rebuild and start
docker-compose up -d --build
```

### Maven

```bash
# Build without tests
mvn clean install -DskipTests

# Run tests
mvn test

# Run specific service
cd order-service && mvn spring-boot:run

# Package as JAR
mvn clean package
```

### MongoDB

```bash
# Connect to MongoDB
docker exec -it mongodb mongosh

# List databases
show dbs

# Use database
use order-db

# List collections
show collections

# Query orders
db.orders.find().pretty()

# Count orders
db.orders.count()
```

### RabbitMQ

```bash
# List queues
docker exec rabbitmq rabbitmqctl list_queues

# List exchanges
docker exec rabbitmq rabbitmqctl list_exchanges

# Purge queue
docker exec rabbitmq rabbitmqctl purge_queue <queue-name>
```

---

## Interview Day Checklist

- [ ] Application is running and tested
- [ ] Can explain architecture in 2 minutes
- [ ] Can walk through code flow
- [ ] Can demonstrate event flow
- [ ] Know why you chose each technology
- [ ] Can discuss trade-offs
- [ ] Have RabbitMQ management UI ready
- [ ] Can create an order via API
- [ ] Understand Saga pattern
- [ ] Can explain how to scale the system

---

## Getting Help

If you're stuck:

1. Check the [SpringBoot Concepts Guide](docs/SPRINGBOOT_CONCEPTS.md)
2. Check the [Interview Walkthrough](docs/INTERVIEW_WALKTHROUGH.md)
3. Review the code comments (they explain everything!)
4. Check Spring Boot documentation
5. Google the error message

---

## Success Criteria

You're ready when you can:

✅ Start the application without errors  
✅ Create an order via API  
✅ See events in RabbitMQ  
✅ Explain the architecture  
✅ Walk through the code  
✅ Discuss design decisions  
✅ Answer "Why SpringBoot over Express?"  
✅ Explain the Saga pattern  
✅ Discuss scalability  
✅ Show enthusiasm for the technology  

---

**Good luck with your interview! 🚀**

Remember: The interviewers want to see that you understand the concepts, can explain your decisions, and are eager to learn. Be confident and enthusiastic!