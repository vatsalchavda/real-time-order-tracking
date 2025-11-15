# 🚀 Quick Start Guide

## Complete Setup in 5 Minutes

### Prerequisites Check
```bash
# Check if you have everything installed
java -version    # Should show Java 17
mvn -version     # Should show Maven 3.x
node --version   # Should show Node 18.x
docker --version # Should show Docker
```

If anything is missing, see `LOCAL_DEVELOPMENT_GUIDE.md` for installation instructions.

---

## Step-by-Step Startup

### 1️⃣ Start Infrastructure (MongoDB & RabbitMQ)
```bash
cd ~/projects/real-time-order-tracking

# Start MongoDB and RabbitMQ in background
docker compose up -d mongodb rabbitmq

# Verify they're running
docker compose ps

# You should see:
# mongodb    running    0.0.0.0:27017
# rabbitmq   running    0.0.0.0:5672, 0.0.0.0:15672
```

**Wait 30 seconds** for services to be fully ready.

---

### 2️⃣ Build All Backend Services
```bash
# This builds all 5 services in correct order:
# 1. common (shared library)
# 2. order-service
# 3. inventory-service
# 4. notification-service
# 5. api-gateway

./build.sh

# You'll see:
# ✓ Common module built successfully
# ✓ Order Service built successfully
# ✓ Inventory Service built successfully
# ✓ Notification Service built successfully
# ✓ API Gateway built successfully
```

**This takes 2-3 minutes** depending on your machine.

---

### 3️⃣ Start All Backend Services
```bash
# This starts all 4 services:
# - Order Service (port 8081)
# - Inventory Service (port 8082)
# - Notification Service (port 8083)
# - API Gateway (port 8080)

./start-dev.sh

# You'll see:
# ✓ Order Service started (PID: 12345)
# ✓ Inventory Service started (PID: 12346)
# ✓ Notification Service started (PID: 12347)
# ✓ API Gateway started (PID: 12348)
```

**Wait 1 minute** for all services to start.

---

### 4️⃣ Start Frontend (New Terminal)
```bash
# Open a NEW terminal window
cd ~/projects/real-time-order-tracking/frontend

# Install dependencies (first time only)
npm install

# Start React development server
npm start

# Browser will automatically open to http://localhost:3000
```

---

## ✅ Verify Everything is Running

### Check Services
```bash
# Check if all services are responding
curl http://localhost:8081/actuator/health  # Order Service
curl http://localhost:8082/actuator/health  # Inventory Service
curl http://localhost:8083/actuator/health  # Notification Service
curl http://localhost:8080/actuator/health  # API Gateway

# All should return: {"status":"UP"}
```

### Access Points
Open these URLs in your browser:

1. **Frontend Application**
   - http://localhost:3000
   - Create orders, see real-time updates

2. **Swagger API Documentation**
   - http://localhost:8081/swagger-ui/index.html
   - Interactive API testing

3. **RabbitMQ Management**
   - http://localhost:15672
   - Username: `guest`
   - Password: `guest`
   - Watch messages flow through queues

4. **API Gateway**
   - http://localhost:8080/api/orders
   - Main entry point for all requests

---

## 🎯 Test the Complete Workflow

### 1. Create an Order (via UI)
1. Go to http://localhost:3000
2. Fill in the form:
   - Customer Name: "John Doe"
   - Product Name: "Laptop"
   - Quantity: 1
   - Price: 1299.99
3. Click "Create Order"
4. Order appears with status "PENDING"

### 2. Watch Event Flow (RabbitMQ)
1. Go to http://localhost:15672
2. Login with guest/guest
3. Click "Queues" tab
4. See messages in:
   - `order.created` queue
   - `inventory.check.requested` queue

### 3. Check Order Status
1. Refresh the frontend
2. Order status should change to "CONFIRMED"
3. This happens because Inventory Service processed the event

### 4. Test Status Changes
1. Click "→ SHIPPED" button on an order
2. Order status changes to "SHIPPED"
3. Click "→ DELIVERED" button
4. Order status changes to "DELIVERED"

### 5. Test Delete
1. Click the 🗑️ button on an order
2. Confirm deletion
3. Order is removed from the list

---

## 🛑 Stop Everything

### Stop Backend Services
```bash
# Gracefully stop all backend services
./stop-dev.sh

# You'll see:
# ✓ API Gateway stopped
# ✓ Notification Service stopped
# ✓ Inventory Service stopped
# ✓ Order Service stopped
```

### Stop Frontend
```bash
# In the frontend terminal, press:
Ctrl + C
```

### Stop Infrastructure (Optional)
```bash
# Stop MongoDB and RabbitMQ
docker compose down

# Stop and remove all data (clean slate)
docker compose down -v
```

---

## 🔄 Daily Development Workflow

### Morning Startup
```bash
# 1. Start infrastructure
docker compose up -d mongodb rabbitmq

# 2. Start backend services
./start-dev.sh

# 3. Start frontend (new terminal)
cd frontend && npm start
```

### Make Changes
```bash
# After changing backend code:
./build.sh           # Rebuild
./stop-dev.sh        # Stop old services
./start-dev.sh       # Start new services

# After changing frontend code:
# React auto-reloads, no restart needed
```

### Evening Shutdown
```bash
# Stop backend
./stop-dev.sh

# Stop frontend (Ctrl+C in terminal)

# Stop infrastructure
docker compose down
```

---

## 📊 View Logs

### Backend Service Logs
```bash
# View logs in real-time
tail -f logs/order-service.log
tail -f logs/inventory-service.log
tail -f logs/notification-service.log
tail -f logs/api-gateway.log

# View last 50 lines
tail -n 50 logs/order-service.log
```

### Docker Logs
```bash
# MongoDB logs
docker compose logs -f mongodb

# RabbitMQ logs
docker compose logs -f rabbitmq

# All logs
docker compose logs -f
```

---

## 🐛 Troubleshooting

### Port Already in Use
```bash
# Find what's using port 8081
sudo lsof -i :8081

# Kill the process
kill -9 <PID>

# Or change port in application.properties
```

### Services Won't Start
```bash
# Check if infrastructure is running
docker compose ps

# Restart infrastructure
docker compose restart mongodb rabbitmq

# Check logs for errors
tail -f logs/order-service.log
```

### Build Fails
```bash
# Clean everything
mvn clean

# Delete Maven cache (nuclear option)
rm -rf ~/.m2/repository

# Rebuild
./build.sh
```

### Frontend Won't Start
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

---

## 🎓 Interview Demo Workflow

### Preparation (Before Interview)
```bash
# 1. Start everything
docker compose up -d mongodb rabbitmq
./start-dev.sh
cd frontend && npm start

# 2. Create 2-3 sample orders
# 3. Test status changes
# 4. Open Swagger UI
# 5. Open RabbitMQ UI
```

### During Interview
1. **Show Architecture** (2 min)
   - Explain microservices
   - Show docker-compose.yml
   - Explain event-driven communication

2. **Demo UI** (2 min)
   - Create order
   - Show status changes
   - Delete order

3. **Show Swagger** (2 min)
   - Open http://localhost:8081/swagger-ui/index.html
   - Test endpoints
   - Show API documentation

4. **Show Event Flow** (2 min)
   - Open RabbitMQ UI
   - Create order
   - Watch messages flow

5. **Show Code** (5 min)
   - OrderController.java
   - OrderService.java
   - Event publishing/consuming

6. **Live Coding** (5 min)
   - Add new endpoint
   - Rebuild and test
   - Show it works

---

## 📝 Useful Commands Cheat Sheet

```bash
# Build
./build.sh                    # Build all services
./test.sh                     # Run all tests

# Start/Stop
./start-dev.sh                # Start all backend services
./stop-dev.sh                 # Stop all backend services
docker compose up -d          # Start infrastructure
docker compose down           # Stop infrastructure

# Logs
tail -f logs/order-service.log           # View logs
docker compose logs -f mongodb           # Docker logs

# Testing
curl http://localhost:8080/api/orders    # Test API
curl http://localhost:8081/actuator/health  # Health check

# Git
git status                    # Check changes
git add -A                    # Stage all changes
git commit -m "message"       # Commit
git push origin main          # Push to GitHub

# MongoDB
docker exec -it mongodb mongosh          # Connect to MongoDB
use orderdb                              # Switch database
db.orders.find().pretty()                # View orders

# Cleanup
docker compose down -v        # Remove all data
rm -rf logs/*                 # Clear logs
```

---

## 🎉 You're All Set!

Your system is now running with:
- ✅ 4 Backend microservices
- ✅ React frontend
- ✅ MongoDB database
- ✅ RabbitMQ message broker
- ✅ Swagger API documentation
- ✅ Complete event-driven workflow

**Next:** Practice the demo workflow 10 times before your interview!

---

**Need Help?** Check `LOCAL_DEVELOPMENT_GUIDE.md` for detailed explanations.

**Author:** Vatsal Chavda  
**Email:** vatsalchavda2@gmail.com