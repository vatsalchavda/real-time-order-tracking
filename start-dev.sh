#!/bin/bash

# Start Development Script for Real-Time Order Tracking System
# Author: Vatsal Chavda <vatsalchavda2@gmail.com>
# Description: Starts all backend services in development mode

set -e  # Exit on any error

echo "=========================================="
echo "Starting Real-Time Order Tracking System"
echo "=========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
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

print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

# Check if JAR files exist
check_jar_files() {
    local all_exist=true
    
    if [ ! -f "backend/order-service/target/order-service-1.0.0.jar" ]; then
        print_error "Order Service JAR not found"
        all_exist=false
    fi
    
    if [ ! -f "backend/inventory-service/target/inventory-service-1.0.0.jar" ]; then
        print_error "Inventory Service JAR not found"
        all_exist=false
    fi
    
    if [ ! -f "backend/notification-service/target/notification-service-1.0.0.jar" ]; then
        print_error "Notification Service JAR not found"
        all_exist=false
    fi
    
    if [ ! -f "backend/api-gateway/target/api-gateway-1.0.0.jar" ]; then
        print_error "API Gateway JAR not found"
        all_exist=false
    fi
    
    if [ "$all_exist" = false ]; then
        print_warning "Please run ./build.sh first to build all services"
        exit 1
    fi
}

# Check if Docker is running
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        print_error "Docker is not running. Please start Docker first."
        exit 1
    fi
}

# Check if infrastructure services are running
check_infrastructure() {
    print_info "Checking infrastructure services..."
    
    # Check MongoDB
    if ! docker compose ps | grep -q "mongodb.*running"; then
        print_warning "MongoDB is not running. Starting infrastructure services..."
        docker compose up -d mongodb rabbitmq
        print_info "Waiting for services to be ready (30 seconds)..."
        sleep 30
    fi
    
    # Check RabbitMQ
    if ! docker compose ps | grep -q "rabbitmq.*running"; then
        print_warning "RabbitMQ is not running. Starting infrastructure services..."
        docker compose up -d mongodb rabbitmq
        print_info "Waiting for services to be ready (30 seconds)..."
        sleep 30
    fi
    
    print_success "Infrastructure services are running"
}

# Create logs directory
mkdir -p logs

# Check prerequisites
print_info "Checking prerequisites..."
check_docker
check_jar_files
check_infrastructure
echo ""

# Start Order Service
print_info "Starting Order Service on port 8081..."
nohup java -jar backend/order-service/target/order-service-1.0.0.jar > logs/order-service.log 2>&1 &
ORDER_PID=$!
echo $ORDER_PID > logs/order-service.pid
print_success "Order Service started (PID: $ORDER_PID)"
echo ""

# Wait for Order Service to be ready
print_info "Waiting for Order Service to be ready..."
sleep 10

# Start Inventory Service
print_info "Starting Inventory Service on port 8082..."
nohup java -jar backend/inventory-service/target/inventory-service-1.0.0.jar > logs/inventory-service.log 2>&1 &
INVENTORY_PID=$!
echo $INVENTORY_PID > logs/inventory-service.pid
print_success "Inventory Service started (PID: $INVENTORY_PID)"
echo ""

# Wait for Inventory Service to be ready
print_info "Waiting for Inventory Service to be ready..."
sleep 10

# Start Notification Service
print_info "Starting Notification Service on port 8083..."
nohup java -jar backend/notification-service/target/notification-service-1.0.0.jar > logs/notification-service.log 2>&1 &
NOTIFICATION_PID=$!
echo $NOTIFICATION_PID > logs/notification-service.pid
print_success "Notification Service started (PID: $NOTIFICATION_PID)"
echo ""

# Wait for Notification Service to be ready
print_info "Waiting for Notification Service to be ready..."
sleep 10

# Start API Gateway
print_info "Starting API Gateway on port 8080..."
nohup java -jar backend/api-gateway/target/api-gateway-1.0.0.jar > logs/api-gateway.log 2>&1 &
GATEWAY_PID=$!
echo $GATEWAY_PID > logs/api-gateway.pid
print_success "API Gateway started (PID: $GATEWAY_PID)"
echo ""

# Wait for API Gateway to be ready
print_info "Waiting for API Gateway to be ready..."
sleep 10

echo ""
echo "=========================================="
print_success "All services started successfully!"
echo "=========================================="
echo ""
echo "Service URLs:"
echo "  • API Gateway:          http://localhost:8080"
echo "  • Order Service:        http://localhost:8081"
echo "  • Inventory Service:    http://localhost:8082"
echo "  • Notification Service: http://localhost:8083"
echo ""
echo "Infrastructure URLs:"
echo "  • MongoDB:              mongodb://localhost:27017"
echo "  • RabbitMQ Management:  http://localhost:15672 (guest/guest)"
echo ""
echo "API Documentation:"
echo "  • Swagger UI:           http://localhost:8081/swagger-ui.html"
echo ""
echo "Process IDs saved in logs/ directory"
echo "Logs available in logs/ directory"
echo ""
echo "To view logs:"
echo "  tail -f logs/order-service.log"
echo "  tail -f logs/inventory-service.log"
echo "  tail -f logs/notification-service.log"
echo "  tail -f logs/api-gateway.log"
echo ""
echo "To stop all services:"
echo "  ./stop-dev.sh"
echo ""
echo "Next step:"
echo "  cd frontend && npm start"
echo ""
