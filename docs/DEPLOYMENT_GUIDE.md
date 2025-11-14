# 🚀 Complete Deployment Guide

## Deploy Your Project Live in 3 Days

This guide walks you through deploying your Order Management System to the cloud with a live URL you can share with interviewers.

---

## 📋 Table of Contents

1. [Deployment Options Comparison](#deployment-options-comparison)
2. [Option 1: Railway (Recommended - Easiest)](#option-1-railway-recommended)
3. [Option 2: Render (Free Tier)](#option-2-render)
4. [Option 3: AWS (Most Professional)](#option-3-aws)
5. [MongoDB Atlas Setup](#mongodb-atlas-setup)
6. [CloudAMQP Setup (RabbitMQ)](#cloudamqp-setup)
7. [Environment Variables](#environment-variables)
8. [Testing Your Deployment](#testing-your-deployment)
9. [Troubleshooting](#troubleshooting)

---

## Deployment Options Comparison

| Platform | Cost | Ease | Time | Best For |
|----------|------|------|------|----------|
| **Railway** | $5/month | ⭐⭐⭐⭐⭐ | 30 min | Quick demo, interviews |
| **Render** | Free | ⭐⭐⭐⭐ | 45 min | Free tier, basic demo |
| **AWS** | ~$10/month | ⭐⭐⭐ | 2 hours | Professional portfolio |
| **Heroku** | $7/month | ⭐⭐⭐⭐ | 30 min | Simple deployment |

**Recommendation for Interview**: Use Railway - it's fast, reliable, and professional.

---

## Option 1: Railway (Recommended)

### Why Railway?
- ✅ Easiest deployment
- ✅ Automatic HTTPS
- ✅ Free $5 credit (enough for testing)
- ✅ GitHub integration
- ✅ Environment variables management
- ✅ Logs and monitoring

### Step-by-Step Railway Deployment

#### Step 1: Create Railway Account (2 minutes)

1. Go to https://railway.app
2. Click "Start a New Project"
3. Sign up with GitHub
4. Verify your email

#### Step 2: Set Up MongoDB Atlas (5 minutes)

See [MongoDB Atlas Setup](#mongodb-atlas-setup) section below.

#### Step 3: Set Up CloudAMQP (5 minutes)

See [CloudAMQP Setup](#cloudamqp-setup) section below.

#### Step 4: Prepare Your Code (5 minutes)

1. **Create Dockerfile for Order Service**

```dockerfile
# backend/order-service/Dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy parent pom and common module
COPY ../pom.xml ./pom.xml
COPY ../common ./common

# Copy order-service
COPY order-service/pom.xml ./order-service/pom.xml
COPY order-service/src ./order-service/src

# Build
WORKDIR /app/order-service
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/order-service/target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
```

2. **Create Dockerfile for Frontend**

```dockerfile
# frontend/Dockerfile
FROM node:18-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

# Production stage
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

3. **Create nginx.conf**

```nginx
# frontend/nginx.conf
server {
    listen 80;
    server_name _;
    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://order-service:8081;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
    }
}
```

#### Step 5: Deploy to Railway (10 minutes)

1. **Push Code to GitHub**
```bash
git init
git add .
git commit -m "Initial commit"
git branch -M main
git remote add origin https://github.com/yourusername/order-management-system.git
git push -u origin main
```

2. **Create Railway Project**
   - Go to Railway dashboard
   - Click "New Project"
   - Select "Deploy from GitHub repo"
   - Choose your repository

3. **Add Services**
   - Click "New Service"
   - Select "order-service"
   - Railway will auto-detect Dockerfile

4. **Configure Environment Variables**
   - Click on order-service
   - Go to "Variables" tab
   - Add:
     ```
     SPRING_DATA_MONGODB_URI=<your-mongodb-atlas-uri>
     SPRING_RABBITMQ_HOST=<cloudamqp-host>
     SPRING_RABBITMQ_PORT=5672
     SPRING_RABBITMQ_USERNAME=<cloudamqp-username>
     SPRING_RABBITMQ_PASSWORD=<cloudamqp-password>
     SPRING_RABBITMQ_VIRTUAL_HOST=<cloudamqp-vhost>
     ```

5. **Deploy Frontend**
   - Click "New Service"
   - Select "frontend"
   - Add environment variable:
     ```
     VITE_API_URL=https://your-order-service.railway.app
     ```

6. **Get Your URLs**
   - Railway will provide URLs like:
     - Backend: `https://order-service-production-xxxx.up.railway.app`
     - Frontend: `https://frontend-production-xxxx.up.railway.app`

#### Step 6: Test Your Deployment (5 minutes)

```bash
# Test backend
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

# Visit frontend
open https://your-frontend.railway.app
```

**Total Time: ~30 minutes**  
**Cost: $5/month (first $5 free)**

---

## Option 2: Render (Free Tier)

### Why Render?
- ✅ Completely free tier
- ✅ Automatic HTTPS
- ✅ GitHub integration
- ⚠️ Services sleep after 15 min inactivity
- ⚠️ Slower cold starts

### Step-by-Step Render Deployment

#### Step 1: Create Render Account

1. Go to https://render.com
2. Sign up with GitHub
3. Verify email

#### Step 2: Deploy Backend

1. **Create New Web Service**
   - Click "New +"
   - Select "Web Service"
   - Connect your GitHub repo
   - Select "order-service" directory

2. **Configure Service**
   ```
   Name: order-service
   Environment: Docker
   Region: Oregon (US West)
   Branch: main
   Dockerfile Path: backend/order-service/Dockerfile
   ```

3. **Add Environment Variables**
   ```
   SPRING_DATA_MONGODB_URI=<mongodb-atlas-uri>
   SPRING_RABBITMQ_HOST=<cloudamqp-host>
   SPRING_RABBITMQ_PORT=5672
   SPRING_RABBITMQ_USERNAME=<cloudamqp-username>
   SPRING_RABBITMQ_PASSWORD=<cloudamqp-password>
   ```

4. **Select Plan**
   - Choose "Free" plan
   - Click "Create Web Service"

#### Step 3: Deploy Frontend

1. **Create Static Site**
   - Click "New +"
   - Select "Static Site"
   - Connect GitHub repo
   - Select "frontend" directory

2. **Configure Build**
   ```
   Build Command: npm install && npm run build
   Publish Directory: dist
   ```

3. **Add Environment Variable**
   ```
   VITE_API_URL=https://order-service.onrender.com
   ```

4. **Deploy**
   - Click "Create Static Site"

**Your URLs:**
- Backend: `https://order-service.onrender.com`
- Frontend: `https://your-app.onrender.com`

**Total Time: ~45 minutes**  
**Cost: FREE**

---

## Option 3: AWS (Most Professional)

### Why AWS?
- ✅ Industry standard
- ✅ Impressive on resume
- ✅ Full control
- ⚠️ More complex
- ⚠️ Costs ~$10/month

### AWS Services Needed

1. **EC2** - Virtual servers
2. **RDS** - Managed database (or use MongoDB Atlas)
3. **S3** - Frontend hosting
4. **CloudFront** - CDN
5. **Route 53** - DNS (optional)

### Step-by-Step AWS Deployment

#### Step 1: Create AWS Account

1. Go to https://aws.amazon.com
2. Create account (requires credit card)
3. Free tier: 12 months free for many services

#### Step 2: Launch EC2 Instance

1. **Go to EC2 Dashboard**
   - Search "EC2" in AWS Console
   - Click "Launch Instance"

2. **Configure Instance**
   ```
   Name: order-management-backend
   AMI: Ubuntu Server 22.04 LTS
   Instance Type: t2.micro (free tier)
   Key Pair: Create new (download .pem file)
   Security Group: Allow ports 22, 8081, 8082
   Storage: 20 GB
   ```

3. **Launch Instance**

#### Step 3: Connect to EC2

```bash
# Make key file secure
chmod 400 your-key.pem

# Connect via SSH
ssh -i your-key.pem ubuntu@<your-ec2-public-ip>
```

#### Step 4: Install Dependencies on EC2

```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Install Java 17
sudo apt install openjdk-17-jdk -y

# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker ubuntu

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Logout and login again for docker group to take effect
exit
ssh -i your-key.pem ubuntu@<your-ec2-public-ip>
```

#### Step 5: Deploy Application

```bash
# Clone your repository
git clone https://github.com/yourusername/order-management-system.git
cd order-management-system

# Create .env file
cat > .env << EOF
MONGODB_URI=<your-mongodb-atlas-uri>
RABBITMQ_HOST=<cloudamqp-host>
RABBITMQ_PORT=5672
RABBITMQ_USER=<cloudamqp-username>
RABBITMQ_PASSWORD=<cloudamqp-password>
EOF

# Build and run
docker-compose up -d

# Check logs
docker-compose logs -f
```

#### Step 6: Deploy Frontend to S3

1. **Create S3 Bucket**
   - Go to S3 Console
   - Click "Create bucket"
   - Name: `your-app-frontend`
   - Uncheck "Block all public access"
   - Enable static website hosting

2. **Build and Upload Frontend**
```bash
# On your local machine
cd frontend
npm run build

# Install AWS CLI
# Mac: brew install awscli
# Windows: Download from AWS website

# Configure AWS CLI
aws configure
# Enter your Access Key ID and Secret

# Upload to S3
aws s3 sync dist/ s3://your-app-frontend --acl public-read
```

3. **Get Website URL**
   - Go to S3 bucket properties
   - Find "Static website hosting" section
   - Copy the endpoint URL

**Your URLs:**
- Backend: `http://<ec2-public-ip>:8081`
- Frontend: `http://your-app-frontend.s3-website-us-east-1.amazonaws.com`

**Total Time: ~2 hours**  
**Cost: ~$10/month**

---

## MongoDB Atlas Setup

### Step 1: Create Account (2 minutes)

1. Go to https://www.mongodb.com/cloud/atlas
2. Click "Try Free"
3. Sign up with email or Google
4. Verify email

### Step 2: Create Cluster (3 minutes)

1. **Choose Plan**
   - Select "Shared" (FREE)
   - Choose cloud provider: AWS
   - Region: Choose closest to you
   - Cluster Name: `order-management`

2. **Create Cluster**
   - Click "Create Cluster"
   - Wait 3-5 minutes for provisioning

### Step 3: Configure Access (2 minutes)

1. **Create Database User**
   - Go to "Database Access"
   - Click "Add New Database User"
   - Username: `admin`
   - Password: Generate secure password (save it!)
   - Database User Privileges: "Read and write to any database"
   - Click "Add User"

2. **Whitelist IP Address**
   - Go to "Network Access"
   - Click "Add IP Address"
   - Click "Allow Access from Anywhere" (0.0.0.0/0)
   - Click "Confirm"

### Step 4: Get Connection String (1 minute)

1. Go to "Database" → "Connect"
2. Choose "Connect your application"
3. Copy connection string:
   ```
   mongodb+srv://admin:<password>@order-management.xxxxx.mongodb.net/?retryWrites=true&w=majority
   ```
4. Replace `<password>` with your actual password
5. Add database name: `/order-db` before the `?`
   ```
   mongodb+srv://admin:yourpassword@order-management.xxxxx.mongodb.net/order-db?retryWrites=true&w=majority
   ```

**Total Time: ~8 minutes**  
**Cost: FREE**

---

## CloudAMQP Setup (RabbitMQ)

### Step 1: Create Account (2 minutes)

1. Go to https://www.cloudamqp.com
2. Click "Sign Up"
3. Sign up with email or GitHub

### Step 2: Create Instance (2 minutes)

1. **Create New Instance**
   - Click "Create New Instance"
   - Name: `order-management-mq`
   - Plan: "Little Lemur" (FREE)
   - Region: Choose closest to you
   - Click "Create Instance"

### Step 3: Get Connection Details (1 minute)

1. Click on your instance
2. Copy connection details:
   ```
   URL: amqps://username:password@host/vhost
   Host: host.cloudamqp.com
   Port: 5672
   Username: username
   Password: password
   Vhost: vhost
   ```

3. **For Spring Boot, you need:**
   ```
   SPRING_RABBITMQ_HOST=host.cloudamqp.com
   SPRING_RABBITMQ_PORT=5672
   SPRING_RABBITMQ_USERNAME=username
   SPRING_RABBITMQ_PASSWORD=password
   SPRING_RABBITMQ_VIRTUAL_HOST=vhost
   ```

**Total Time: ~5 minutes**  
**Cost: FREE**

---

## Environment Variables

### For Local Development (.env)

```bash
# MongoDB
MONGODB_URI=mongodb://localhost:27017/order-db

# RabbitMQ
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USER=guest
RABBITMQ_PASSWORD=guest
```

### For Production (Railway/Render/AWS)

```bash
# MongoDB Atlas
SPRING_DATA_MONGODB_URI=mongodb+srv://admin:password@cluster.mongodb.net/order-db?retryWrites=true&w=majority

# CloudAMQP
SPRING_RABBITMQ_HOST=host.cloudamqp.com
SPRING_RABBITMQ_PORT=5672
SPRING_RABBITMQ_USERNAME=your-username
SPRING_RABBITMQ_PASSWORD=your-password
SPRING_RABBITMQ_VIRTUAL_HOST=your-vhost

# Application
SERVER_PORT=8081
SPRING_PROFILES_ACTIVE=prod
```

### Update application.yml for Production

```yaml
spring:
  data:
    mongodb:
      uri: ${SPRING_DATA_MONGODB_URI}
  cloud:
    stream:
      binders:
        rabbit:
          environment:
            spring:
              rabbitmq:
                host: ${SPRING_RABBITMQ_HOST}
                port: ${SPRING_RABBITMQ_PORT}
                username: ${SPRING_RABBITMQ_USERNAME}
                password: ${SPRING_RABBITMQ_PASSWORD}
                virtual-host: ${SPRING_RABBITMQ_VIRTUAL_HOST:}
```

---

## Testing Your Deployment

### 1. Health Check

```bash
curl https://your-app.railway.app/actuator/health
```

Expected response:
```json
{
  "status": "UP",
  "components": {
    "mongo": {"status": "UP"},
    "rabbit": {"status": "UP"}
  }
}
```

### 2. Create Order

```bash
curl -X POST https://your-app.railway.app/api/orders \
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
```

### 3. Get Orders

```bash
curl https://your-app.railway.app/api/orders
```

### 4. Check Frontend

Open your frontend URL in browser and test:
- Create order form works
- Orders list displays
- Real-time updates work

---

## Troubleshooting

### Issue: MongoDB Connection Failed

**Error**: `MongoTimeoutException: Timed out after 30000 ms`

**Solution**:
1. Check MongoDB Atlas IP whitelist (should be 0.0.0.0/0)
2. Verify connection string is correct
3. Check username/password
4. Ensure database name is in the URI

### Issue: RabbitMQ Connection Failed

**Error**: `IOException: Connection refused`

**Solution**:
1. Verify CloudAMQP credentials
2. Check virtual host is set correctly
3. Ensure port is 5672 (not 5671)
4. Check if CloudAMQP instance is running

### Issue: Application Won't Start

**Error**: `Port 8081 already in use`

**Solution**:
1. Change port in environment variables
2. Or kill process using the port

### Issue: Frontend Can't Connect to Backend

**Error**: `CORS error` or `Network error`

**Solution**:
1. Add CORS configuration in Spring Boot:
```java
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                    .allowedOrigins("*")
                    .allowedMethods("*");
            }
        };
    }
}
```

2. Update frontend API URL to match backend URL

---

## Cost Summary

### Free Option (Render + MongoDB Atlas + CloudAMQP)
- **Total: $0/month**
- ⚠️ Services sleep after inactivity
- ⚠️ Slower performance

### Recommended Option (Railway + MongoDB Atlas + CloudAMQP)
- **Total: $5/month**
- ✅ Always on
- ✅ Fast performance
- ✅ Professional

### Professional Option (AWS + MongoDB Atlas + CloudAMQP)
- **Total: ~$10/month**
- ✅ Full control
- ✅ Impressive on resume
- ✅ Industry standard

---

## Interview Preparation

### Share These URLs with Interviewers

Create a document with:

```
Project: Event-Driven Order Management System

Live Demo:
- Frontend: https://your-app.railway.app
- Backend API: https://your-api.railway.app
- Health Check: https://your-api.railway.app/actuator/health

GitHub Repository:
- https://github.com/yourusername/order-management-system

Documentation:
- Architecture: [Link to README]
- API Docs: [Link to Swagger]
- Technical Guide: [Link to docs]

Test Credentials:
- No authentication required for demo
- Sample API calls provided in README
```

### Demo Script for Interview

1. **Show Frontend** (30 seconds)
   - "This is the live application running on Railway"
   - Create an order through the UI

2. **Show Backend API** (30 seconds)
   - "Here's the REST API endpoint"
   - Show health check
   - Show Swagger documentation

3. **Explain Architecture** (1 minute)
   - "The backend is SpringBoot microservices"
   - "MongoDB Atlas for database"
   - "CloudAMQP for message broker"
   - "Event-driven communication between services"

4. **Show Code** (2 minutes)
   - Walk through GitHub repository
   - Highlight key files
   - Explain design decisions

---

## Next Steps

1. **Deploy to Railway** (30 minutes)
2. **Test thoroughly** (15 minutes)
3. **Update README** with live URLs
4. **Prepare demo script**
5. **Practice walkthrough**

**You're now ready to impress interviewers with a live, production-ready application!** 🚀