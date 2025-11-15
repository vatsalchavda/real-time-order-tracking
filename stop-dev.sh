#!/bin/bash

# Stop Development Script for Real-Time Order Tracking System
# Author: Vatsal Chavda <vatsalchavda2@gmail.com>
# Description: Stops all running backend services

echo "=========================================="
echo "Stopping Real-Time Order Tracking System"
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

# Function to stop a service
stop_service() {
    local service_name=$1
    local pid_file="logs/${service_name}.pid"
    
    if [ -f "$pid_file" ]; then
        local pid=$(cat "$pid_file")
        if ps -p $pid > /dev/null 2>&1; then
            print_info "Stopping $service_name (PID: $pid)..."
            kill $pid
            sleep 2
            
            # Force kill if still running
            if ps -p $pid > /dev/null 2>&1; then
                print_warning "Force stopping $service_name..."
                kill -9 $pid
            fi
            
            rm "$pid_file"
            print_success "$service_name stopped"
        else
            print_warning "$service_name is not running (PID: $pid)"
            rm "$pid_file"
        fi
    else
        print_warning "$service_name PID file not found"
    fi
}

# Stop all services
print_info "Stopping all services..."
echo ""

stop_service "api-gateway"
stop_service "notification-service"
stop_service "inventory-service"
stop_service "order-service"

echo ""
echo "=========================================="
print_success "All services stopped!"
echo "=========================================="
echo ""
echo "Infrastructure services (MongoDB, RabbitMQ) are still running."
echo "To stop infrastructure services:"
echo "  docker compose down"
echo ""
echo "To stop infrastructure and remove volumes:"
echo "  docker compose down -v"
echo ""

# Made with Bob
