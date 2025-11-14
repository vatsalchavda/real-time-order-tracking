# 🚀 Railway Deployment Fix - Circular Dependency Issue

## Problem
The backend has incomplete microservices (inventory-service, notification-service, api-gateway) that cause Maven build failures.

## Solution: Deploy Only Order Service

Instead of building the entire multi-module project, deploy just the order-service.

---

## 📋 Step-by-Step Fix

### Option 1: Use Railway Root Directory (RECOMMENDED)

1. **In Railway Dashboard:**
   - Go to your service settings
   - Find **"Root Directory"** setting
   - Set it to: `backend/order-service`
   - Click **"Save"**

2. **Update Railway Configuration:**
   Railway will now build only the order-service, not the parent POM.

3. **The build will use the Dockerfile:**
   - Railway will detect `backend/order-service/Dockerfile`
   - This builds just the order-service independently

---

### Option 2: Delete Incomplete Services (Alternative)

If you want to keep the multi-module structure:

```bash
cd /Users/vatsalchavda/projects/real-time-order-tracking

# Remove incomplete services
rm -rf backend/inventory-service
rm -rf backend/notification-service
rm -rf backend/api-gateway

# Commit and push
git add -A
git commit -m "Remove incomplete services for deployment"
git push origin main
```

Then Railway will build successfully with just common + order-service.

---

## 🎯 Recommended Approach: Option 1

**Why?**
- ✅ Keeps all code in repo for interview demonstration
- ✅ Deploys only what's needed
- ✅ No need to delete files
- ✅ Cleaner deployment

**Steps:**
1. Go to Railway → Your Service → Settings
2. Scroll to **"Root Directory"**
3. Enter: `backend/order-service`
4. Click **"Redeploy"**

---

## 📝 After Successful Deployment

### Add Environment Variables:

```env
SPRING_DATA_MONGODB_URI=mongodb+srv://username:password@cluster.mongodb.net/order-db?retryWrites=true&w=majority
SPRING_RABBITMQ_HOST=your-host.rmq.cloudamqp.com
SPRING_RABBITMQ_PORT=5672
SPRING_RABBITMQ_USERNAME=your-username
SPRING_RABBITMQ_PASSWORD=your-password
SPRING_RABBITMQ_VIRTUAL_HOST=your-vhost
SERVER_PORT=8080
```

### Test Your Deployment:

```bash
# Health check
curl https://your-app.railway.app/actuator/health

# Get orders
curl https://your-app.railway.app/api/orders

# Create order
curl -X POST https://your-app.railway.app/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "John Doe",
    "customerEmail": "john@example.com",
    "items": [
      {
        "productId": "PROD-001",
        "productName": "Laptop",
        "quantity": 1,
        "price": 999.99
      }
    ]
  }'
```

---

## 🔍 Why This Works

**The Problem:**
- Parent POM tries to build all modules
- Incomplete modules have POMs but no main classes
- Maven fails with circular dependency

**The Solution:**
- Deploy only `backend/order-service` directory
- Uses the Dockerfile in that directory
- Builds independently without parent POM issues

---

## ✅ Expected Result

After setting Root Directory to `backend/order-service`:

```
✓ Detecting Dockerfile
✓ Building Docker image
✓ Installing dependencies
✓ Building JAR
✓ Starting application
✓ Application running on port 8080
```

---

## 💡 For Your Interview

You can explain:
> "I structured the project as a multi-module Maven application to demonstrate microservices architecture. For deployment, I used Railway's root directory feature to deploy the order-service independently, which is a common practice in microservices - deploying services separately rather than as a monolith."

This shows:
- ✅ Understanding of microservices deployment
- ✅ Practical cloud deployment experience
- ✅ Problem-solving with platform-specific features

---

## 🚀 Do This Now!

1. Go to Railway Dashboard
2. Click your service
3. Go to **Settings**
4. Find **"Root Directory"**
5. Enter: `backend/order-service`
6. Click **"Redeploy"**

The build should succeed! 🎉