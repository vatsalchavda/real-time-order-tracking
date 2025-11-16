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
# Support a top-level convenience flag to build the whole reactor
BUILD_ALL=false
if [ "$1" = "--all" ] || [ "$1" = "-a" ]; then
    BUILD_ALL=true
fi

# If requested, build all modules using the parent POM (faster and less error-prone)
if [ "$BUILD_ALL" = true ]; then
    print_info "Building entire backend using parent POM (backend/pom.xml)..."
    mvn -f backend/pom.xml -DskipTests package
    print_success "Parent build completed"
    echo ""
    echo "JAR files created (examples):"
    echo "  • backend/order-service/target/order-service-*.jar"
    echo "  • backend/inventory-service/target/inventory-service-*.jar"
    echo ""
    exit 0
fi

# Navigate to backend directory and build individual modules that exist
cd backend

print_info "Step 1/3: Building Common module (if present)..."
if [ -d common ]; then
    (cd common && mvn clean install -DskipTests)
    print_success "Common module built (if present)"
else
    print_info "No 'common' module found, skipping"
fi
echo ""

print_info "Step 2/3: Building Order Service (if present)..."
if [ -d order-service ]; then
    (cd order-service && mvn clean package -DskipTests)
    print_success "Order Service built (if present)"
else
    print_info "No 'order-service' module found, skipping"
fi
echo ""

print_info "Step 3/3: Building Inventory Service (if present)..."
if [ -d inventory-service ]; then
    (cd inventory-service && mvn clean package -DskipTests)
    print_success "Inventory Service built (if present)"
else
    print_info "No 'inventory-service' module found, skipping"
fi
echo ""

# Return to root directory
cd ..

echo ""
echo "=========================================="
print_success "All services built successfully!"
echo "=========================================="
echo ""
echo "JAR files created:"
echo "  • backend/order-service/target/order-service-*.jar"
echo "  • backend/inventory-service/target/inventory-service-*.jar"
echo ""
echo "Next steps:"
echo "  1. Start infrastructure: docker compose up -d mongodb rabbitmq"
echo "  2. Start services: ./start-dev.sh"
echo "  3. Start frontend: cd frontend && npm start"
echo ""
