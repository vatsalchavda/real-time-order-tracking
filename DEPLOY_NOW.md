# 🚀 Deploy Your Application NOW - Step by Step

Follow these exact steps to deploy your application online in the next 2 hours.

---

## ⏱️ Time Estimate: 2 Hours Total

- MongoDB Atlas Setup: 15 minutes
- CloudAMQP Setup: 15 minutes  
- Railway Deployment: 30 minutes
- Testing: 15 minutes
- **Buffer**: 45 minutes

---

## 📋 Prerequisites Checklist

Before starting, make sure you have:

- [ ] GitHub account (create at https://github.com/signup if needed)
- [ ] Credit card (for Railway - they give $5 free credit, won't charge unless you exceed)
- [ ] Email access (for verifications)
- [ ] This project code on your computer

---

## STEP 1: Set Up MongoDB Atlas (15 minutes)

### 1.1 Create Account (3 minutes)

1. Open https://www.mongodb.com/cloud/atlas/register
2. Sign up with:
   - Email: your email
   - Password: create a strong password
   - Click "Create your Atlas account"
3. Check your email and verify
4. Choose "I'm learning MongoDB" when asked
5. Select "JavaScript" as your preferred language
6. Click "Finish"

### 1.2 Create Free Cluster (5 minutes)

**If you have an existing MongoDB Atlas account:**

1. Log in to https://cloud.mongodb.com
2. Look at the left sidebar - click on **"Database"**
3. You'll see your existing clusters (if any)
4. Click the green **"+ Create"** button (top right)
5. OR click **"Build a Database"** button
6. Choose **"M0 FREE"** tier (should show "FREE" badge)
7. Provider: **AWS**
8. Region: Choose closest to you (e.g., **us-east-1** for North America)
9. Cluster Name: `order-management` (or leave default)
10. Click **"Create Deployment"** or **"Create Cluster"**
11. **WAIT 3-5 MINUTES** - Don't close the page!

**If you see "Create a deployment" page (new account):**

1. Choose **"M0 FREE"** (should be selected by default)
2. Provider: **AWS**
3. Region: Choose closest to you (e.g., **us-east-1** for North America)
4. Cluster Name: `order-management` (or leave default)
5. Click **"Create Deployment"**
6. **WAIT 3-5 MINUTES** - Don't close the page!

### 1.3 Create Database User (2 minutes)

1. You'll see "Security Quickstart"
2. Authentication Method: **Username and Password**
3. Username: `admin`
4. Password: Click **"Autogenerate Secure Password"**
5. **COPY THE PASSWORD AND SAVE IT SOMEWHERE SAFE!**
6. Click **"Create Database User"**

### 1.4 Set Up Network Access (2 minutes)

1. Still on Security Quickstart page
2. Under "Where would you like to connect from?"
3. Click **"My Local Environment"**
4. Click **"Add My Current IP Address"**
5. Then click **"Add Entry"** again
6. In the IP Address field, enter: `0.0.0.0/0`
7. Description: `Allow all`
8. Click **"Add Entry"**
9. Click **"Finish and Close"**

### 1.5 Get Connection String (3 minutes)

1. Click **"Go to Overview"** or **"Database"** in left sidebar
2. Click **"Connect"** button
3. Choose **"Connect your application"**
4. Driver: **Java**
5. Version: **4.3 or later**
6. Copy the connection string (looks like):
   ```
   mongodb+srv://admin:<password>@cluster0.xxxxx.mongodb.net/?retryWrites=true&w=majority
   ```
7. **SAVE THIS STRING!**
8. Replace `<password>` with the password you saved earlier
9. Add `/order-db` before the `?`:
   ```
   mongodb+srv://admin:YOUR_PASSWORD@cluster0.xxxxx.mongodb.net/order-db?retryWrites=true&w=majority
   ```

**✅ MongoDB Atlas is ready! Save your connection string.**

---

## STEP 2: Set Up CloudAMQP (15 minutes)

### 2.1 Create Account (3 minutes)

1. Open https://customer.cloudamqp.com/signup
2. Sign up with:
   - Email: your email
   - Password: create a password
   - Click "Sign Up"
3. Check your email and verify

### 2.2 Create Free Instance (5 minutes)

1. After login, click **"Create New Instance"**
2. Name: `order-management-mq`
3. Plan: **"Little Lemur (Free)"** - Make sure it says FREE
4. Region: Choose closest to you
5. Click **"Select Region"**
6. Review and click **"Create instance"**
7. **WAIT 1-2 MINUTES**

### 2.3 Get Connection Details (7 minutes)

1. Click on your instance name (`order-management-mq`)
2. You'll see the instance details page
3. **COPY AND SAVE THESE VALUES:**

   ```
   URL: amqps://username:password@host.cloudamqp.com/vhost
   
   You need to extract:
   - Host: host.cloudamqp.com (the part after @ and before /)
   - Port: 5672 (always this for AMQP)
   - Username: (the part after amqps:// and before :)
   - Password: (the part after : and before @)
   - Vhost: (the part after the last /)
   ```

4. Example:
   ```
   If URL is: amqps://abcdefgh:xyz123@goose.cloudamqp.com/abcdefgh
   
   Then:
   - Host: goose.cloudamqp.com
   - Port: 5672
   - Username: abcdefgh
   - Password: xyz123
   - Vhost: abcdefgh
   ```

**✅ CloudAMQP is ready! Save all connection details.**

---

## STEP 3: Push Code to GitHub (10 minutes)

### 3.1 Initialize Git (if not already done)

```bash
cd /Users/vatsalchavda/projects/real-time-order-tracking

# Check if git is initialized
git status

# If you see "not a git repository", run:
git init
```

### 3.2 Create GitHub Repository

1. Go to https://github.com/new
2. Repository name: `order-management-system`
3. Description: `Event-Driven Order Management System with SpringBoot and React`
4. Choose **Public** (so you can share with interviewers)
5. **DO NOT** check "Initialize with README"
6. Click **"Create repository"**

### 3.3 Push Code

```bash
# Add all files
git add .

# Commit
git commit -m "Initial commit: Event-Driven Order Management System"

# Add remote (replace YOUR_USERNAME with your GitHub username)
git remote add origin https://github.com/YOUR_USERNAME/order-management-system.git

# Push
git branch -M main
git push -u origin main
```

**If you get authentication error:**
1. Go to https://github.com/settings/tokens
2. Click "Generate new token (classic)"
3. Give it a name: "Railway Deployment"
4. Check "repo" scope
5. Click "Generate token"
6. Copy the token
7. Use it as password when pushing

**✅ Code is on GitHub!**

---

## STEP 4: Deploy to Railway (30 minutes)

### 4.1 Create Railway Account (3 minutes)

1. Go to https://railway.app
2. Click **"Start a New Project"**
3. Click **"Login with GitHub"**
4. Authorize Railway to access your GitHub
5. You'll get **$5 free credit** (no credit card needed initially)

### 4.2 Create New Project (2 minutes)

1. Click **"New Project"**
2. Select **"Deploy from GitHub repo"**
3. Choose your repository: `order-management-system`
4. Railway will start analyzing your repo

### 4.3 Deploy Order Service (10 minutes)

1. Railway should detect the Dockerfile in `backend/order-service`
2. If not, click **"Add Service"** → **"GitHub Repo"** → Select your repo
3. Click on the service (it will be named after your repo)
4. Go to **"Settings"** tab
5. Under "Build", set:
   - **Root Directory**: `backend/order-service`
   - **Dockerfile Path**: `Dockerfile`
6. Click **"Variables"** tab
7. Click **"New Variable"** and add these one by one:

   ```
   SPRING_DATA_MONGODB_URI=<your-mongodb-connection-string>
   SPRING_RABBITMQ_HOST=<your-cloudamqp-host>
   SPRING_RABBITMQ_PORT=5672
   SPRING_RABBITMQ_USERNAME=<your-cloudamqp-username>
   SPRING_RABBITMQ_PASSWORD=<your-cloudamqp-password>
   SPRING_RABBITMQ_VIRTUAL_HOST=<your-cloudamqp-vhost>
   SERVER_PORT=8081
   ```

8. Click **"Deploy"** (top right)
9. **WAIT 5-10 MINUTES** for build to complete
10. Once deployed, go to **"Settings"** → **"Networking"**
11. Click **"Generate Domain"**
12. **COPY YOUR BACKEND URL** (e.g., `https://order-service-production-xxxx.up.railway.app`)

### 4.4 Deploy Frontend (10 minutes)

1. Click **"New Service"** in your project
2. Select **"GitHub Repo"** → Choose your repo again
3. Click on the new service
4. Go to **"Settings"** tab
5. Under "Build", set:
   - **Root Directory**: `frontend`
   - **Dockerfile Path**: `Dockerfile`
6. Click **"Variables"** tab
7. Add this variable:
   ```
   VITE_API_URL=<your-backend-url-from-step-4.3>
   ```
   Example: `VITE_API_URL=https://order-service-production-xxxx.up.railway.app`

8. Click **"Deploy"**
9. **WAIT 5-10 MINUTES** for build
10. Once deployed, go to **"Settings"** → **"Networking"**
11. Click **"Generate Domain"**
12. **COPY YOUR FRONTEND URL** (e.g., `https://frontend-production-xxxx.up.railway.app`)

### 4.5 Fix CORS (5 minutes)

Your backend needs to allow requests from your frontend domain.

1. Go back to your Order Service in Railway
2. Click **"Variables"** tab
3. Add this variable:
   ```
   CORS_ALLOWED_ORIGINS=<your-frontend-url>
   ```
   Example: `CORS_ALLOWED_ORIGINS=https://frontend-production-xxxx.up.railway.app`

4. The service will automatically redeploy

**✅ Your application is deployed!**

---

## STEP 5: Test Your Deployment (15 minutes)

### 5.1 Test Backend Health

```bash
# Replace with your actual backend URL
curl https://your-order-service.up.railway.app/actuator/health
```

**Expected response:**
```json
{
  "status": "UP",
  "components": {
    "mongo": {"status": "UP"},
    "rabbit": {"status": "UP"}
  }
}
```

### 5.2 Test Creating an Order

```bash
curl -X POST https://your-order-service.up.railway.app/api/orders \
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

**Expected response:** Order object with status "PENDING"

### 5.3 Test Frontend

1. Open your frontend URL in browser
2. You should see the Order Management System
3. Try creating an order through the UI
4. Check if it appears in the orders list

### 5.4 Verify Data in MongoDB Atlas

1. Go to MongoDB Atlas dashboard
2. Click **"Database"** → **"Browse Collections"**
3. You should see `order-db` database
4. Click on `orders` collection
5. You should see your test orders

### 5.5 Verify Events in CloudAMQP

1. Go to CloudAMQP dashboard
2. Click on your instance
3. You should see message activity in the graphs

**✅ Everything is working!**

---

## STEP 6: Update Your README (5 minutes)

Add these lines to the top of your README.md:

```markdown
## 🌐 Live Demo

**Frontend**: https://your-frontend-url.up.railway.app  
**Backend API**: https://your-backend-url.up.railway.app  
**Health Check**: https://your-backend-url.up.railway.app/actuator/health

### Try It Out

Create an order:
\`\`\`bash
curl -X POST https://your-backend-url.up.railway.app/api/orders \\
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

Commit and push:
```bash
git add README.md
git commit -m "Add live demo URLs"
git push
```

---

## 🎉 YOU'RE DONE!

### Your Live URLs:

- **Frontend**: https://your-frontend.up.railway.app
- **Backend**: https://your-backend.up.railway.app
- **GitHub**: https://github.com/YOUR_USERNAME/order-management-system

### Share These with Interviewers:

Create a document with:

```
Project: Event-Driven Order Management System

Live Demo: https://your-frontend.up.railway.app
GitHub: https://github.com/YOUR_USERNAME/order-management-system
API Health: https://your-backend.up.railway.app/actuator/health

Technologies:
- Backend: SpringBoot 3.2, Java 17
- Frontend: React 18, TypeScript
- Database: MongoDB Atlas
- Message Broker: CloudAMQP (RabbitMQ)
- Deployment: Railway (Docker containers)

Key Features:
✅ Event-Driven Architecture (Saga Pattern)
✅ Microservices with independent databases
✅ Real-time order processing
✅ RESTful APIs
✅ Production deployment
```

---

## 🐛 Troubleshooting

### Issue: Railway build fails

**Solution:**
1. Check Railway logs (click on service → "Deployments" → Click on failed deployment)
2. Common issues:
   - Wrong Dockerfile path → Check "Root Directory" in Settings
   - Missing environment variables → Check "Variables" tab
   - Build timeout → Railway free tier has limits, try again

### Issue: Backend health check fails

**Solution:**
1. Check environment variables are correct
2. Verify MongoDB connection string
3. Verify CloudAMQP credentials
4. Check Railway logs for errors

### Issue: Frontend can't connect to backend

**Solution:**
1. Check VITE_API_URL is set correctly in frontend variables
2. Check CORS_ALLOWED_ORIGINS is set in backend variables
3. Make sure both services are deployed and running

### Issue: MongoDB connection timeout

**Solution:**
1. Check MongoDB Atlas IP whitelist includes 0.0.0.0/0
2. Verify connection string is correct
3. Check username/password

### Issue: RabbitMQ connection refused

**Solution:**
1. Verify all CloudAMQP credentials
2. Check virtual host is correct
3. Ensure port is 5672

---

## 💰 Cost Breakdown

- **MongoDB Atlas**: FREE (M0 tier, 512MB storage)
- **CloudAMQP**: FREE (Little Lemur plan, 1M messages/month)
- **Railway**: $5/month (first $5 free, then pay-as-you-go)

**Total for first month**: $0 (using free credits)  
**After free credits**: ~$5/month

---

## 📞 Need Help?

If you get stuck:

1. **Check Railway Logs**:
   - Go to your service
   - Click "Deployments"
   - Click on the deployment
   - Check logs for errors

2. **Check Environment Variables**:
   - Make sure all variables are set correctly
   - No extra spaces
   - Correct values

3. **Test Locally First**:
   - Make sure it works locally before deploying
   - Use docker-compose to test

4. **Railway Discord**:
   - https://discord.gg/railway
   - Very helpful community

---

## ✅ Success Checklist

- [ ] MongoDB Atlas cluster created
- [ ] CloudAMQP instance created
- [ ] Code pushed to GitHub
- [ ] Backend deployed to Railway
- [ ] Frontend deployed to Railway
- [ ] Health check returns UP
- [ ] Can create orders via API
- [ ] Frontend loads and works
- [ ] Orders appear in MongoDB
- [ ] Events flow through RabbitMQ
- [ ] README updated with live URLs

---

## 🎯 Next Steps

1. **Test thoroughly** - Create multiple orders, check database
2. **Share with friends** - Get feedback
3. **Prepare demo** - Practice showing it to interviewers
4. **Monitor usage** - Check Railway dashboard for usage
5. **Add features** - Authentication, more services, etc.

---

**🚀 Congratulations! Your application is live and ready to impress interviewers!**

**Live Demo**: https://your-frontend.up.railway.app  
**GitHub**: https://github.com/YOUR_USERNAME/order-management-system

Good luck with your interview! 🍀