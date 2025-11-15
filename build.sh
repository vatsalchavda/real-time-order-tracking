#!/bin/bash

# Build Script for Real-Time Order Tracking System
# Author: Vatsal Chavda <vatsalchavda2@gmail.com>
# Description: Builds all backend services in the correct order

set -e  # Exit on any error

echo "=========================================="
echo "Building Real-Time Order Tracking System"
echo "=========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Function to print colored output
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_info() {
    echo -e "${BLUE}→ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    print_error "Maven is not installed. Please install Maven first."
    exit 1
fi

# Check if Java is installed
if ! command -v java &> /dev/null; then
    print_error "Java is not installed. Please install Java 17 first."
    exit 1
fi

print_info "Java Version:"
java -version
echo ""

print_info "Maven Version:"
mvn -version
echo ""

# Navigate to backend directory
cd backend

# Step 1: Build Common Module
print_info "Step 1/5: Building Common Module..."
cd common
mvn clean install -DskipTests
if [ $? -eq 0 ]; then
    print_success "Common module built successfully"
else
    print_error "Failed to build common module"
    exit 1
fi
cd ..
echo ""

# Step 2: Build Order Service
print_info "Step 2/5: Building Order Service..."
cd order-service
mvn clean package -DskipTests
if [ $? -eq 0 ]; then
    print_success "Order Service built successfully"
else
    print_error "Failed to build Order Service"
    exit 1
fi
cd ..
echo ""

# Step 3: Build Inventory Service
print_info "Step 3/5: Building Inventory Service..."
cd inventory-service
mvn clean package -DskipTests
if [ $? -eq 0 ]; then
    print_success "Inventory Service built successfully"
else
    print_error "Failed to build Inventory Service"
    exit 1
fi
cd ..
echo ""

# Step 4: Build Notification Service
print_info "Step 4/5: Building Notification Service..."
cd notification-service
mvn clean package -DskipTests
if [ $? -eq 0 ]; then
    print_success "Notification Service built successfully"
else
    print_error "Failed to build Notification Service"
    exit 1
fi
cd ..
echo ""

# Step 5: Build API Gateway
print_info "Step 5/5: Building API Gateway..."
cd api-gateway
mvn clean package -DskipTests
if [ $? -eq 0 ]; then
    print_success "API Gateway built successfully"
else
    print_error "Failed to build API Gateway"
    exit 1
fi
cd ..
echo ""

# Return to root directory
cd ..

echo ""
echo "=========================================="
print_success "All services built successfully!"
echo "=========================================="
echo ""
echo "JAR files created:"
echo "  • backend/order-service/target/order-service-1.0.0.jar"
echo "  • backend/inventory-service/target/inventory-service-1.0.0.jar"
echo "  • backend/notification-service/target/notification-service-1.0.0.jar"
echo "  • backend/api-gateway/target/api-gateway-1.0.0.jar"
echo ""
echo "Next steps:"
echo "  1. Start infrastructure: docker compose up -d mongodb rabbitmq"
echo "  2. Start services: ./start-dev.sh"
echo "  3. Start frontend: cd frontend && npm start"
echo ""

# Made with Bob
