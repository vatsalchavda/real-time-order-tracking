#!/bin/bash

# Test Script for Real-Time Order Tracking System
# Author: Vatsal Chavda <vatsalchavda2@gmail.com>
# Description: Runs tests for all backend services

set -e  # Exit on any error

echo "=========================================="
echo "Testing Real-Time Order Tracking System"
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

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    print_error "Maven is not installed. Please install Maven first."
    exit 1
fi

# Navigate to backend directory
cd backend

# Test Common Module
print_info "Testing Common Module..."
cd common
mvn test
if [ $? -eq 0 ]; then
    print_success "Common module tests passed"
else
    print_error "Common module tests failed"
    exit 1
fi
cd ..
echo ""

# Test Order Service
print_info "Testing Order Service..."
cd order-service
mvn test
if [ $? -eq 0 ]; then
    print_success "Order Service tests passed"
else
    print_error "Order Service tests failed"
    exit 1
fi
cd ..
echo ""

# Test Inventory Service
print_info "Testing Inventory Service..."
cd inventory-service
mvn test
if [ $? -eq 0 ]; then
    print_success "Inventory Service tests passed"
else
    print_error "Inventory Service tests failed"
    exit 1
fi
cd ..
echo ""

# Test Notification Service
print_info "Testing Notification Service..."
cd notification-service
mvn test
if [ $? -eq 0 ]; then
    print_success "Notification Service tests passed"
else
    print_error "Notification Service tests failed"
    exit 1
fi
cd ..
echo ""

# Test API Gateway
print_info "Testing API Gateway..."
cd api-gateway
mvn test
if [ $? -eq 0 ]; then
    print_success "API Gateway tests passed"
else
    print_error "API Gateway tests failed"
    exit 1
fi
cd ..
echo ""

# Return to root directory
cd ..

echo ""
echo "=========================================="
print_success "All tests passed!"
echo "=========================================="
echo ""
