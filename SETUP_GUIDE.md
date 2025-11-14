# 🚀 Complete Setup Guide

## From Zero to Deployed in 3 Days

This guide walks you through setting up, running, and deploying your Order Management System.

---

## 📋 Prerequisites

Before you begin, install these tools:

### Required Software

1. **Java 17**
   ```bash
   # Mac
   brew install openjdk@17
   
   # Windows
   # Download from https://adoptium.net/
   
   # Verify
   java -version  # Should show 17.x.x
   ```

2. **Maven 3.8+**
   ```bash
   # Mac
   brew install maven
   
   # Windows
   # Download from https://maven.apache.org/download.cgi
   
   # Verify
   mvn -version
   ```

3. **Node.js 18+**
   ```bash
   # Mac
   brew install node
   
   # Windows
   # Download from https://nodejs.org/
   
   # Verify
   node --version
   npm --version
   ```

4. **Docker & Docker Compose**
   ```bash
   # Mac
   brew install --cask docker
   
   # Windows
   # Download Docker Desktop from https://www.docker.com/products/docker-desktop
   
   # Verify
   docker --version
   docker-compose --version
   ```

5. **Git**
   ```bash
   # Mac
   brew install git
   
   # Windows
   # Download from https://git-scm.com/
   
   # Verify
   git --version
   ```

---

## 🏗️ Day 1: Local Setup (4-6 hours)

### Step 1: Clone or Initialize Project (5 minutes)

```bash
# If you have the code
cd /path/to/real-time-order-tracking

# If starting fresh, the code is already created in your directory
cd /Users/vatsalchavda/projects/real-time-order-tracking
```

### Step 2: Install Frontend Dependencies (5 minutes)

```bash
cd frontend
npm install
cd ..
```

**Expected output**: `added XXX packages`

### Step 3: Build Backend (10 minutes)

```bash
cd backend
mvn clean install -DskipTests
```

**Expected output**: `BUILD SUCCESS`

**If build fails:**
```bash
# Clear Maven cache
rm -rf ~/.m2/repository

# Try again
mvn clean install -DskipTests -U
```

### Step 4: Start Infrastructure with Docker (5 minutes)

```bash
# From project root
docker-compose up -d mongodb rabbitmq

# Wait 30 seconds for services to start

# Verify services are running
docker-compose ps
```

**Expected output:**
```
NAME                COMMAND                  SERVICE             STATUS
oms-mongodb         "docker-entrypoint.s…"   mongodb             Up
oms-rabbitmq        "docker-entrypoint.s…"   rabbitmq            Up
```

### Step 5: Run Backend Services (5 minutes)

**Terminal 1 - Order Service:**
```bash
cd backend/order-service
mvn spring-boot:run
```

Wait for: `Started OrderServiceApplication in X seconds`

**Terminal 2 - Inventory Service (Optional for now):**
```bash
cd backend/inventory-service
mvn spring-boot:run
```

### Step 6: Run Frontend (2 minutes)

**Terminal 3:**
```bash
cd frontend
npm run dev
```

**Expected output:**
```
  VITE v5.0.8  ready in 500 ms

  ➜  Local:   http://localhost:3000/
  ➜  Network: use --host to expose
```

### Step 7: Test the Application (5 minutes)

1. **Open Browser**: http://localhost:3000
2. **Create an Order**:
   - Customer Name: John Doe
   - Product Name: Laptop
   - Quantity: 1
   - Price: 999.99
   - Click "Create Order"

3. **Verify**:
   - Order appears in "Recent Orders"
   - Status shows "PENDING"

4. **Check Backend**:
   ```bash
   curl http://localhost:8081/actuator/health
   ```
   Should return: `{"status":"UP"}`

5. **Check RabbitMQ**:
   - Open: http://localhost:15672
   - Login: guest/guest
   - Go to "Queues" tab
   - You should see queues with messages

**🎉 Congratulations! Your application is running locally!**

---

## 🌐 Day 2: Cloud Setup (4-6 hours)

### Part A: MongoDB Atlas Setup (15 minutes)

#### 1. Create Account
1. Go to https://www.mongodb.com/cloud/atlas
2. Click "Try Free"
3. Sign up with email
4. Verify email

#### 2. Create Cluster
1. Choose "Shared" (FREE)
2. Provider: AWS
3. Region: Choose closest to you
4. Cluster Name: `order-management`
5. Click "Create Cluster"
6. **Wait 3-5 minutes** for provisioning

#### 3. Create Database User
1. Go to "Database Access" (left sidebar)
2. Click "Add New Database User"
3. Authentication Method: Password
4. Username: `admin`
5. Password: Click "Autogenerate Secure Password" and **SAVE IT**
6. Database User Privileges: "Atlas admin"
7. Click "Add User"

#### 4. Whitelist IP Address
1. Go to "Network Access" (left sidebar)
2. Click "Add IP Address"
3. Click "Allow Access from Anywhere" (0.0.0.0/0)
4. Click "Confirm"

#### 5. Get Connection String
1. Go to "Database" → Click "Connect"
2. Choose "Connect your application"
3. Driver: Java, Version: 4.3 or later
4. Copy connection string:
   ```
   mongodb+srv://admin:<password>@order-management.xxxxx.mongodb.net/?retryWrites=true&w=majority
   ```
5. Replace `<password>` with your actual password
6. Add database name before `?`:
   ```
   mongodb+srv://admin:yourpassword@order-management.xxxxx.mongodb.net/order-db?retryWrites=true&w=majority
   ```

**Save this connection string - you'll need it for deployment!**

### Part B: CloudAMQP Setup (10 minutes)

#### 1. Create Account
1. Go to https://www.cloudamqp.com
2. Click "Sign Up"
3. Sign up with email or GitHub

#### 2. Create Instance
1. Click "Create New Instance"
2. Name: `order-management-mq`
3. Plan: "Little Lemur" (FREE)
4. Region: Choose closest to you
5. Click "Create Instance"

#### 3. Get Connection Details
1. Click on your instance
2. Copy these details:
   ```
   Host: host.cloudamqp.com
   Port: 5672
   Username: your-username
   Password: your-password
   Vhost: your-vhost
   ```

**Save these details - you'll need them for deployment!**

### Part C: Test with Cloud Databases (15 minutes)

1. **Update application.yml**:
   ```bash
   cd backend/order-service/src/main/resources
   ```

2. **Edit application.yml**:
   ```yaml
   spring:
     data:
       mongodb:
         uri: mongodb+srv://admin:yourpassword@cluster.mongodb.net/order-db?retryWrites=true&w=majority
     cloud:
       stream:
         binders:
           rabbit:
             environment:
               spring:
                 rabbitmq:
                   host: host.cloudamqp.com
                   port: 5672
                   username: your-username
                   password: your-password
                   virtual-host: your-vhost
   ```

3. **Restart Order Service**:
   ```bash
   cd backend/order-service
   mvn spring-boot:run
   ```

4. **Test**:
   - Create an order via frontend
   - Check MongoDB Atlas:
     - Go to "Database" → "Browse Collections"
     - You should see `order-db` database with `orders` collection
   - Check CloudAMQP:
     - Go to your instance dashboard
     - You should see message activity

**🎉 Your application now uses cloud databases!**

---

## 🚀 Day 3: Deployment (4-6 hours)

### Option 1: Railway Deployment (Recommended - 30 minutes)

#### Step 1: Prepare for Deployment (10 minutes)

1. **Create Dockerfile for Order Service**:
   ```bash
   cd backend/order-service
   ```

   Create `Dockerfile`:
   ```dockerfile
   FROM maven:3.9-eclipse-temurin-17 AS build
   WORKDIR /app
   
   # Copy parent pom
   COPY ../pom.xml ./pom.xml
   COPY ../common ./common
   COPY pom.xml ./order-service/pom.xml
   COPY src ./order-service/src
   
   WORKDIR /app/order-service
   RUN mvn clean package -DskipTests
   
   FROM eclipse-temurin:17-jre-alpine
   WORKDIR /app
   COPY --from=build /app/order-service/target/*.jar app.jar
   
   EXPOSE 8081
   ENTRYPOINT ["java", "-jar", "app.jar"]
   ```

2. **Create Dockerfile for Frontend**:
   ```bash
   cd frontend
   ```

   Create `Dockerfile`:
   ```dockerfile
   FROM node:18-alpine AS build
   WORKDIR /app
   COPY package*.json ./
   RUN npm install
   COPY . .
   RUN npm run build
   
   FROM nginx:alpine
   COPY --from=build /app/dist /usr/share/nginx/html
   EXPOSE 80
   CMD ["nginx", "-g", "daemon off;"]
   ```

#### Step 2: Push to GitHub (5 minutes)

```bash
# Initialize git (if not already)
git init

# Add all files
git add .

# Commit
git commit -m "Initial commit - Order Management System"

# Create repository on GitHub
# Go to https://github.com/new
# Repository name: order-management-system
# Public or Private: Your choice
# Don't initialize with README

# Add remote
git remote add origin https://github.com/YOUR_USERNAME/order-management-system.git

# Push
git branch -M main
git push -u origin main
```

#### Step 3: Deploy to Railway (15 minutes)

1. **Create Railway Account**:
   - Go to https://railway.app
   - Click "Start a New Project"
   - Sign up with GitHub

2. **Create New Project**:
   - Click "New Project"
   - Select "Deploy from GitHub repo"
   - Choose your repository

3. **Deploy Order Service**:
   - Railway will detect the Dockerfile
   - Click on the service
   - Go to "Settings"
   - Add environment variables:
     ```
     SPRING_DATA_MONGODB_URI=<your-mongodb-atlas-uri>
     SPRING_RABBITMQ_HOST=<cloudamqp-host>
     SPRING_RABBITMQ_PORT=5672
     SPRING_RABBITMQ_USERNAME=<cloudamqp-username>
     SPRING_RABBITMQ_PASSWORD=<cloudamqp-password>
     SPRING_RABBITMQ_VIRTUAL_HOST=<cloudamqp-vhost>
     ```
   - Click "Deploy"

4. **Deploy Frontend**:
   - Click "New Service"
   - Select your repository
   - Choose "frontend" directory
   - Add environment variable:
     ```
     VITE_API_URL=https://your-order-service.railway.app
     ```
   - Click "Deploy"

5. **Get Your URLs**:
   - Order Service: `https://order-service-production-xxxx.up.railway.app`
   - Frontend: `https://frontend-production-xxxx.up.railway.app`

#### Step 4: Test Deployment (5 minutes)

```bash
# Test backend health
curl https://your-order-service.railway.app/actuator/health

# Test creating an order
curl -X POST https://your-order-service.railway.app/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST123",
    "customerName": "John Doe",
    "items": [{
      "productId": "PROD001",
      "productName": "Laptop",
      "quantity": 1,
      "price": 999.99
    }],
    "shippingAddress": "123 Main St"
  }'

# Open frontend in browser
open https://your-frontend.railway.app
```

**🎉 Your application is now live!**

---

## 📝 Post-Deployment Checklist

### Update README with Live URLs

Edit `README.md`:
```markdown
## 🌐 Live Demo

- **Frontend**: https://your-frontend.railway.app
- **Backend API**: https://your-order-service.railway.app
- **Health Check**: https://your-order-service.railway.app/actuator/health

## 🧪 Try It Out

Create an order:
\`\`\`bash
curl -X POST https://your-order-service.railway.app/api/orders \\
  -H "Content-Type: application/json" \\
  -d '{
    "customerId": "CUST123",
    "customerName": "John Doe",
    "items": [{
      "productId": "PROD001",
      "productName": "Laptop",
      "quantity": 1,
      "price": 999.99
    }],
    "shippingAddress": "123 Main St"
  }'
\`\`\`
```

### Create Interview Document

Create `INTERVIEW_LINKS.md`:
```markdown
# Interview Links

## Project Information

**Project Name**: Event-Driven Order Management System  
**GitHub**: https://github.com/YOUR_USERNAME/order-management-system  
**Live Demo**: https://your-frontend.railway.app  
**API Endpoint**: https://your-order-service.railway.app

## Quick Demo

1. **Frontend**: Visit the live demo URL
2. **Create Order**: Fill the form and submit
3. **View Orders**: See real-time updates
4. **API Health**: https://your-order-service.railway.app/actuator/health

## Architecture

- **Backend**: SpringBoot 3.2 microservices
- **Database**: MongoDB Atlas (cloud)
- **Message Broker**: CloudAMQP (RabbitMQ)
- **Frontend**: React 18 with TypeScript
- **Deployment**: Railway (containerized)

## Key Features

✅ Event-Driven Architecture (Saga Pattern)  
✅ Microservices with independent databases  
✅ Real-time updates via message queues  
✅ RESTful APIs with proper error handling  
✅ Production-ready deployment  
✅ Comprehensive documentation

## Documentation

- [SpringBoot Concepts](docs/SPRINGBOOT_CONCEPTS.md)
- [Interview Walkthrough](docs/INTERVIEW_WALKTHROUGH.md)
- [Deployment Guide](docs/DEPLOYMENT_GUIDE.md)
- [API Documentation](README.md#api-documentation)
```

---

## 🎯 Verification Steps

### Local Development
- [ ] Backend builds successfully
- [ ] Frontend runs without errors
- [ ] Can create orders via UI
- [ ] Orders appear in MongoDB
- [ ] Events flow through RabbitMQ
- [ ] Health check returns UP

### Cloud Databases
- [ ] MongoDB Atlas connection works
- [ ] CloudAMQP connection works
- [ ] Data persists in cloud
- [ ] Events flow through cloud broker

### Deployment
- [ ] Code pushed to GitHub
- [ ] Backend deployed to Railway
- [ ] Frontend deployed to Railway
- [ ] Live URLs work
- [ ] Can create orders on live site
- [ ] Health check works on live site

---

## 🐛 Troubleshooting

### Issue: Maven Build Fails

**Error**: `Cannot find symbol` or `package does not exist`

**Solution**:
```bash
# Clear Maven cache
rm -rf ~/.m2/repository

# Rebuild
cd backend
mvn clean install -U -DskipTests
```

### Issue: Frontend Won't Start

**Error**: `Cannot find module 'react'`

**Solution**:
```bash
cd frontend
rm -rf node_modules package-lock.json
npm install
npm run dev
```

### Issue: MongoDB Connection Failed

**Error**: `MongoTimeoutException`

**Solution**:
1. Check MongoDB Atlas IP whitelist (should be 0.0.0.0/0)
2. Verify connection string is correct
3. Check username/password
4. Ensure database name is in the URI

### Issue: RabbitMQ Connection Failed

**Error**: `Connection refused`

**Solution**:
1. Verify CloudAMQP credentials
2. Check virtual host is set correctly
3. Ensure port is 5672
4. Check if CloudAMQP instance is running

### Issue: Railway Deployment Fails

**Error**: `Build failed`

**Solution**:
1. Check Dockerfile syntax
2. Verify environment variables are set
3. Check Railway logs for specific error
4. Ensure dependencies are correct in pom.xml/package.json

---

## 📞 Getting Help

If you're stuck:

1. **Check Documentation**:
   - [SpringBoot Concepts](docs/SPRINGBOOT_CONCEPTS.md)
   - [Deployment Guide](docs/DEPLOYMENT_GUIDE.md)
   - [Interview Walkthrough](docs/INTERVIEW_WALKTHROUGH.md)

2. **Check Logs**:
   ```bash
   # Local backend logs
   cd backend/order-service
   mvn spring-boot:run
   
   # Docker logs
   docker-compose logs -f
   
   # Railway logs
   # Go to Railway dashboard → Your service → Logs
   ```

3. **Common Commands**:
   ```bash
   # Restart everything
   docker-compose down
   docker-compose up -d
   
   # Rebuild backend
   cd backend
   mvn clean install -DskipTests
   
   # Rebuild frontend
   cd frontend
   npm run build
   ```

---

## 🎓 Next Steps

### For Interview Preparation

1. **Practice Demo** (1 hour):
   - Walk through architecture
   - Explain code flow
   - Demonstrate live application
   - Answer common questions

2. **Review Documentation** (2 hours):
   - Read SpringBoot Concepts guide
   - Study Interview Walkthrough
   - Understand design decisions

3. **Prepare Talking Points** (1 hour):
   - Why event-driven architecture?
   - How does Saga pattern work?
   - What are the trade-offs?
   - How would you scale this?

### For Further Development

1. **Add Features**:
   - User authentication (JWT)
   - Payment service
   - Shipping service
   - Order cancellation
   - Email notifications

2. **Improve Code**:
   - Add more tests
   - Implement caching (Redis)
   - Add API documentation (Swagger)
   - Implement circuit breakers
   - Add monitoring (Prometheus)

3. **Enhance Deployment**:
   - Set up CI/CD pipeline
   - Add Kubernetes deployment
   - Implement blue-green deployment
   - Add logging (ELK stack)
   - Set up alerts

---

## ✅ Success Criteria

You're ready for the interview when you can:

- [ ] Start the application locally without errors
- [ ] Create orders and see them in the database
- [ ] Explain the architecture in 2 minutes
- [ ] Walk through the code confidently
- [ ] Demonstrate the live application
- [ ] Answer "Why SpringBoot over Express?"
- [ ] Explain the Saga pattern with examples
- [ ] Discuss scalability and production concerns
- [ ] Show enthusiasm for the technology

---

## 🎉 Congratulations!

You now have:
- ✅ A working local development environment
- ✅ Cloud databases (MongoDB Atlas + CloudAMQP)
- ✅ A live, deployed application
- ✅ Professional GitHub repository
- ✅ Comprehensive documentation
- ✅ Interview-ready demo

**You're ready to impress in your interview!** 🚀

Good luck! 🍀