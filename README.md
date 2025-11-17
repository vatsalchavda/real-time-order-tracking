# 🚀 Event-Driven Order Management System

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-7.0-green.svg)](https://www.mongodb.com/)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3.12-orange.svg)](https://www.rabbitmq.com/) 
[![React](https://img.shields.io/badge/React-18-blue.svg)](https://reactjs.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-blue.svg)](https://docs.docker.com/compose/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A production-ready, event-driven microservices architecture demonstrating modern enterprise patterns including **Saga Pattern**, **Event Sourcing**, **CQRS**, and **Domain-Driven Design**.

## 📋 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [API Documentation](#api-documentation)
- [Service-Specific API Usage](#service-specific-api-usage)
- [Event Flow](#event-flow)
- [Design Patterns](#design-patterns)
- [Documentation](#documentation)
- [Contributing](#contributing)

## 🎯 Overview

This project demonstrates a complete event-driven microservices system for order management. It showcases:

- **Microservices Architecture**: Independent, scalable services
- **Event-Driven Communication**: Asynchronous messaging via RabbitMQ
- **Saga Pattern**: Distributed transaction management
- **Event Sourcing**: Complete audit trail of all state changes
- **CQRS**: Separation of read and write operations
- **Domain-Driven Design**: Rich domain models with business logic

### Why This Project?

Built to demonstrate enterprise-grade software engineering practices and modern architectural patterns used by companies like Netflix, Amazon, and Uber.

## 🏗️ Architecture

### System Architecture (runtime)

```mermaid
%%{init: {"themeVariables": {"fontSize": "16px", "nodePadding": "16", "clusterBkg": "#f9f9f9"}}}%%
flowchart TB
   %% Top-down layout for better readability on narrow screens
   Frontend["Frontend (React)\nport 3000"]
   Order["Order Service\ncontainer: oms-order-service\nport: 8081"]
   Inventory["Inventory Service\ncontainer: oms-inventory-service\nport: 8082"]
   Rabbit["RabbitMQ\ncontainer: oms-rabbitmq\nports: 5672 / 15672"]
   RabbitMgmt["RabbitMQ Management\nport: 15672"]
   Mongo["MongoDB\ncontainer: oms-mongodb\nport: 27017\n(databases: order-db, inventory-db)"]
   MongoExpress["Mongo Express\ncontainer: oms-mongo-express\nport: 8888"]

   Frontend -->|HTTP REST| Order
   Order -->|Publish events| Rabbit
   Rabbit -->|Deliver events| Inventory
   Order -->|Reads / Writes| Mongo
   Inventory -->|Reads / Writes| Mongo
   Mongo --> MongoExpress
   Rabbit --> RabbitMgmt

   subgraph CodeModules["Code (not containers)"]
      Common["common module\n(shared library) - not a running service"]
   end
   Common -.-> Order
   Common -.-> Inventory
```

Notes:

- `common` is a shared code module (library), not a running service/container.
- There is a single MongoDB instance; services use separate logical databases (`order-db`, `inventory-db`).
- The project currently does NOT run an API Gateway or Notification Service — these are listed as "Future Enhancements" and are not started by `docker-compose.yml`.
- Frontend currently polls/auto-refreshes for status updates (no WebSocket push server is deployed).
- RabbitMQ uses exchanges/queues and bindings; events published by Order Service are routed to consumers (e.g., Inventory Service) based on bindings configured in each service's `application.yml`.


## ✨ Features

### Core Features

- ✅ **Order Management**: Create, read, update, delete orders with full lifecycle
- ✅ **Inventory Management**: Real-time stock tracking and reservation
- ✅ **Event-Driven Communication**: Asynchronous messaging via RabbitMQ
- ✅ **Saga Pattern**: Distributed transaction management for order processing
- ✅ **Event Publishing**: Order Service publishes events for other services
- ✅ **Event Listening**: Services listen and react to events
- ✅ **Real-time UI**: React frontend with auto-refresh on status changes

### Technical Features

- 🔐 **Security**: CORS configuration, input validation
- 📊 **Monitoring**: Spring Boot Actuator, health checks, metrics
- 🐳 **Docker**: Docker and Docker Compose for infrastructure
- 🧪 **Testing**: Unit tests with Spring Boot Test
- 📝 **Documentation**: Comprehensive comments, API reference
- 📈 **Scalability**: Event-driven messaging for horizontal scaling
- 🛡️ **Resilience**: Retry logic via Spring Cloud Stream

## 🛠️ Technology Stack

### Backend

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17 | Programming Language |
| Spring Boot | 3.2.0 | Application Framework |
| Spring Cloud Stream | 4.1.0 | Event-Driven Messaging |
| Spring Data MongoDB | 3.2.0 | Database Access |
| RabbitMQ | 3.12 | Message Broker |
| MongoDB | 7.0 | NoSQL Database |
| Lombok | 1.18.30 | Boilerplate Reduction |
| JUnit 5 | 5.10.0 | Testing Framework |

### Frontend

| Technology | Version | Purpose |
|------------|---------|---------|
| React | 18 | UI Framework |
| TypeScript | 5.0 | Type Safety |
| Vite | 5.0 | Build Tool |
| TanStack Query | 5.0 | Data Fetching |
| Tailwind CSS | 3.4 | Styling |
| WebSocket | - | Real-time Updates |

### DevOps

| Technology | Purpose |
|------------|---------|
| Docker | Containerization |
| Docker Compose | Multi-container Orchestration |
| Maven | Build Tool |
| GitHub Actions | CI/CD |

## 🚀 Getting Started

### Prerequisites

- **Docker & Docker Compose** (recommended for quick start)
- **Java 17+** (for manual backend development)
- **Maven 3.8+** (for building backend)
- **Node.js 18+** (for frontend development)
- **Git**

### Quick Start (Docker Compose - Recommended)

1. **Clone the repository**
   ```bash
   git clone https://github.com/vatsalchavda/real-time-order-tracking.git
   cd real-time-order-tracking
   ```

2. **Start all services with one command**
   ```bash
   docker-compose up -d
   ```

3. **Verify all containers are running**
   ```bash
   docker-compose ps
   ```

4. **Access the application**
   - **Frontend UI**: http://localhost:3000
   - **Order Service API**: http://localhost:8081
   - **Inventory Service API**: http://localhost:8082
   - **RabbitMQ Management**: http://localhost:15672 (guest/guest)
   - **MongoDB Express (GUI)**: http://localhost:8888 (admin/admin123)

5. **View service logs**
   ```bash
   # View all logs
   docker-compose logs -f
   
   # View specific service logs
   docker-compose logs -f order-service
   docker-compose logs -f inventory-service
   ```

6. **Stop all services**
   ```bash
   docker-compose down
   ```

### Useful Docker Compose Commands

```bash
# Start services in detached mode
docker-compose up -d

# Stop all services (preserves data)
docker-compose stop

# Start previously stopped services
docker-compose start

# Stop and remove containers (preserves volumes)
docker-compose down

# Stop and remove everything (including data)
docker-compose down -v

# Rebuild images and start
docker-compose up -d --build

# View status
docker-compose ps

# View logs with timestamps
docker-compose logs --timestamps

# Access a service container shell
docker-compose exec order-service /bin/sh
```

## 📁 Project Structure

```
real-time-order-tracking/
├── backend/
│   ├── common/                      # Shared DTOs, Events, Utilities
│   │   └── src/main/java/com/eventdriven/oms/common/
│   │       ├── dto/                 # Data Transfer Objects
│   │       ├── event/               # Event definitions
│   │       └── enums/               # Enums (OrderStatus, etc.)
│   │
│   ├── order-service/               # Order Management Microservice (Port 8081)
│   │   ├── src/main/java/com/eventdriven/oms/orderservice/
│   │   │   ├── controller/          # REST Controllers
│   │   │   ├── service/             # Business Logic & Saga Implementation
│   │   │   ├── repository/          # Data Access Layer
│   │   │   ├── model/               # Domain Entities
│   │   │   ├── event/               # Event Publishers/Listeners
│   │   │   └── config/              # Configuration (OpenAPI, etc.)
│   │   └── resources/
│   │       └── application.yml      # Spring Boot Configuration
│   │
│   ├── inventory-service/           # Inventory Management Microservice (Port 8082)
│   │   ├── src/main/java/com/eventdriven/oms/inventoryservice/
│   │   │   ├── service/             # Business Logic
│   │   │   ├── repository/          # Data Access Layer
│   │   │   ├── model/               # Domain Entities
│   │   │   ├── event/               # Event Listeners
│   │   │   └── config/              # Configuration
│   │   └── resources/
│   │       └── application.yml      # Spring Boot Configuration
│   │
│   ├── pom.xml                      # Maven Parent POM
│   └── Dockerfile                   # Docker configuration for services
│
├── frontend/                        # React Frontend Application
│   ├── src/
│   │   ├── components/              # React Components
│   │   ├── services/                # API Service Layer
│   │   ├── App.tsx                  # Main Application Component
│   │   └── main.tsx                 # Application Entry Point
│   ├── package.json                 # Node.js Dependencies
│   ├── vite.config.ts               # Vite Build Configuration
│   ├── tailwind.config.js           # Tailwind CSS Configuration
│   ├── Dockerfile                   # Docker Image for Frontend
│   └── nginx.conf                   # Nginx Configuration for Production
│
├── docs/                            # Documentation
│   ├── SPRINGBOOT_CONCEPTS.md       # Spring Boot Learning Guide
│   ├── INTERVIEW_WALKTHROUGH.md     # Interview Preparation Guide
│   └── DEPLOYMENT_GUIDE.md          # Deployment Instructions
│
├── docker-compose.yml               # Docker Compose Configuration
├── build.sh                         # Build script for backend services
├── start-dev.sh                     # Start services in development mode
├── stop-dev.sh                      # Stop all services
├── test.sh                          # Run tests
├── QUICK_START.md                   # Quick start guide
├── LOCAL_DEVELOPMENT_GUIDE.md       # Detailed development setup
├── README.md                        # This file
└── .gitignore                       # Git ignore rules
```

## 📚 API Documentation

### Order Service API

#### Create Order
```http
POST http://localhost:8081/api/orders
Content-Type: application/json

{
  "customerId": "CUST123",
  "customerName": "John Doe",
  "items": [
    {
      "productId": "PROD001",
      "productName": "Laptop",
      "quantity": 1,
      "price": 999.99
    }
  ],
  "shippingAddress": "123 Main St"
}

Response: 202 Accepted
{
  "orderId": "abc-123-def-456",
  "customerId": "CUST123",
  "customerName": "John Doe",
  "status": "PENDING",
  "items": [...],
  "totalAmount": 999.99,
  "createdAt": "2024-11-17T10:30:00"
}
```

#### Get All Orders
```http
GET http://localhost:8081/api/orders

Response: 200 OK
[
  {
    "orderId": "abc-123",
    "customerId": "CUST123",
    "status": "CONFIRMED",
    "totalAmount": 999.99,
    ...
  }
]
```

#### Get Order by ID
```http
GET http://localhost:8081/api/orders/{orderId}

Response: 200 OK
{
  "orderId": "abc-123",
  "customerId": "CUST123",
  "customerName": "John Doe",
  "status": "CONFIRMED",
  "items": [...],
  "totalAmount": 999.99
}
```

#### Get Orders by Customer
```http
GET http://localhost:8081/api/orders/customer/{customerId}

Response: 200 OK
[
  {
    "orderId": "abc-123",
    "status": "CONFIRMED",
    ...
  }
]
```

### Inventory Service API

#### Health Check
```http
GET http://localhost:8082/actuator/health

Response: 200 OK
{
  "status": "UP",
  "components": {
    "mongo": { "status": "UP" },
    "rabbit": { "status": "UP" }
  }
}
```

### Available Endpoints

**Order Service (http://localhost:8081)**
- `GET /api/orders` - Get all orders
- `GET /api/orders/{orderId}` - Get order by ID
- `GET /api/orders/customer/{customerId}` - Get orders by customer
- `POST /api/orders` - Create new order
- `GET /actuator/health` - Health check
- `GET /actuator/metrics` - Metrics

**Inventory Service (http://localhost:8082)**
- `GET /actuator/health` - Health check
- `GET /actuator/metrics` - Metrics

## 🔄 Event Flow

### Event Types

| Event | Publisher | Subscribers | Purpose |
|-------|-----------|-------------|---------|
| `ORDER_CREATED` | Order Service | Internal tracking | New order created |
| `INVENTORY_CHECK_REQUESTED` | Order Service | Inventory Service | Request stock check |
| `INVENTORY_RESERVED` | Inventory Service | Order Service | Stock reserved successfully |
| `INVENTORY_INSUFFICIENT` | Inventory Service | Order Service | Not enough stock (compensation) |
| `ORDER_CONFIRMED` | Order Service | Internal tracking | Order confirmed after inventory check |
| `ORDER_CANCELLED` | Order Service | Internal tracking | Order cancelled (compensation) |

### Event Structure

```json
{
  "eventId": "550e8400-e29b-41d4-a716-446655440000",
  "eventType": "ORDER_CREATED",
  "order": {
    "orderId": "abc-123",
    "customerId": "CUST123",
    "customerName": "John Doe",
    "items": [...],
    "status": "PENDING",
    "totalAmount": 999.99
  },
  "timestamp": "2024-11-17T10:30:00Z",
  "source": "order-service",
  "correlationId": "abc-123"
}
```

### How Events Work in This System

```mermaid
sequenceDiagram
   participant UI as Frontend
   participant OS as Order Service
   participant MQ as RabbitMQ
   participant IS as Inventory Service

   UI->>OS: POST /api/orders (create order)
   OS->>OS: Save order (PENDING)
   OS->>MQ: ORDER_CREATED
   OS->>MQ: INVENTORY_CHECK_REQUESTED

   MQ->>IS: INVENTORY_CHECK_REQUESTED
   IS->>IS: Check stock

   alt Stock available
      IS->>MQ: INVENTORY_RESERVED
      MQ->>OS: INVENTORY_RESERVED
      OS->>OS: Update order (CONFIRMED)
   else Stock insufficient
      IS->>MQ: INVENTORY_INSUFFICIENT
      MQ->>OS: INVENTORY_INSUFFICIENT
      OS->>OS: Update order (CANCELLED)
   end

   Note over OS,UI: Frontend polls for status updates (no WebSocket deployed)
```

This sequence shows the choreography-based Saga: the Order Service publishes events and the Inventory Service reacts. The frontend updates its view by polling or auto-refreshing; a Notification Service / WebSocket push is a future enhancement.

- Mermaid source: `docs/diagrams/event-flow.mmd`
- Architecture source: `docs/diagrams/architecture.mmd`
- To generate an SVG fallback locally (requires Node.js >= 20 and mermaid-cli):

```bash
# using npx (downloads mermaid-cli temporarily)
# or install globally: npm install -g @mermaid-js/mermaid-cli
npx @mermaid-js/mermaid-cli -i docs/diagrams/event-flow.mmd -o docs/diagrams/event-flow.svg
```

If you want, I can add the generated SVG to the repo (I attempted generation here but the environment's Node version prevented a successful build).

## 🎨 Design Patterns

### Implemented Patterns

1. **Saga Pattern**: Choreography-based distributed transaction management
   - Order Service publishes events
   - Inventory Service reacts to events
   - Compensation transactions on failure (order cancellation)

2. **Event-Driven Architecture**: Asynchronous communication via RabbitMQ
   - Loose coupling between services
   - Services can be deployed independently
   - Scalable event processing

3. **Repository Pattern**: Data access abstraction
   - Spring Data MongoDB repositories
   - Clean separation of concerns

4. **Dependency Injection**: Loose coupling through IoC container
   - Constructor injection with Spring
   - Testability and maintainability

5. **Builder Pattern**: Object construction (via Lombok)
   - Fluent API for creating objects
   - Reduced boilerplate code

6. **Spring Cloud Stream**: Message abstraction pattern
   - Works with RabbitMQ (or Kafka)
   - Consistent API regardless of broker

### Architectural Patterns

- **Microservices Architecture**: Independent Order and Inventory services
- **Event-Driven Architecture**: Services communicate via events
- **Database per Service**: Each service has its own MongoDB database
- **Layered Architecture**: Controller → Service → Repository layers
- **Asynchronous Processing**: Saga pattern for distributed transactions

## 📖 Documentation

### Learning Resources

- **[SpringBoot Concepts Guide](docs/SPRINGBOOT_CONCEPTS.md)**: Complete SpringBoot reference
- **[Interview Walkthrough](docs/INTERVIEW_WALKTHROUGH.md)**: How to present this project
- **[API Documentation](docs/API_DOCUMENTATION.md)**: Detailed API reference

### Key Concepts Explained

- **Dependency Injection & IoC**: How Spring manages beans
- **Event-Driven Architecture**: Asynchronous communication
- **Saga Pattern**: Distributed transactions
- **Spring Cloud Stream**: Message broker abstraction
- **MongoDB with Spring Data**: NoSQL database access
- **REST API Best Practices**: HTTP methods, status codes
---

## 🚀 Future Enhancements

### Planned Features

1. **Notification Service**
   - WebSocket-based real-time notifications
   - Push notifications to frontend when order status changes
   - Email notifications for order confirmations
   - Notification persistence and retry logic

2. **API Gateway**
   - Centralized API routing and load balancing
   - Authentication/Authorization layer (JWT)
   - Rate limiting and throttling
   - Request/response logging and monitoring
   - Request validation and sanitization

3. **Advanced Event Features**
   - Event Sourcing for complete audit trail
   - Event replay capability for new services
   - Dead Letter Queue (DLQ) handling
   - Event versioning and evolution

4. **CQRS Pattern**
   - Separate read and write models
   - Read replicas for optimized queries
   - Eventual consistency handling
   - Query-specific services

5. **Enhanced Security**
   - OAuth2/OIDC integration
   - Role-based access control (RBAC)
   - API key management
   - Encryption for sensitive data
   - Input validation and sanitization

6. **Observability & Monitoring**
   - Distributed tracing (Jaeger)
   - Centralized logging (ELK stack)
   - Metrics collection (Prometheus)
   - Performance monitoring and alerts
   - Health check dashboards

7. **Testing Enhancements**
   - Contract testing between services
   - Performance/load testing
   - Chaos engineering tests
   - End-to-end integration tests
   - UI testing with Cypress

8. **Kubernetes Deployment**
   - Helm charts for deployment
   - Auto-scaling based on metrics
   - Service mesh (Istio)
   - Persistent volume management
   - ConfigMaps and Secrets management

9. **Additional Features**
   - Order search and filtering
   - Inventory forecasting
   - Payment integration
   - Shipping integration
   - Order history and analytics
   - Admin dashboard

10. **Performance Optimization**
    - Caching layer (Redis)
    - Database indexing optimization
    - Query optimization
    - Batch processing for reports
    - Async processing for heavy operations

### How to Contribute These Features

Interested in adding these features? Follow these steps:

1. Choose a feature from the list above
2. Create a feature branch: `git checkout -b feature/notification-service`
3. Implement the feature following the existing architecture patterns
4. Add tests for your implementation
5. Update documentation
6. Submit a Pull Request

---

## 🧪 Testing

### Run All Tests

```bash
# Run tests for entire project
mvn test

# Run tests with output
mvn test -X

# Run specific test class
mvn test -Dtest=OrderControllerTest

# Run tests in specific module
cd backend/order-service
mvn test

# Generate coverage report
mvn test jacoco:report
```

### Available Tests

- **Order Service Tests**: `backend/order-service/src/test/`
  - `OrderControllerTest`: REST API endpoint tests
  - `OrderServiceTest`: Business logic tests
  
- **Inventory Service Tests**: `backend/inventory-service/src/test/`

### Test Structure

- **Unit Tests**: Test individual components in isolation
- **Integration Tests**: Test service interaction with Spring context
- **Repository Tests**: Test MongoDB operations

## 🔧 Configuration

### Environment Variables

The application uses environment variables from `docker-compose.yml`. Key variables:

```bash
# MongoDB
SPRING_DATA_MONGODB_URI=mongodb://admin:admin123@mongodb:27017/order-db?authSource=admin

# RabbitMQ
SPRING_RABBITMQ_HOST=rabbitmq
SPRING_RABBITMQ_PORT=5672
SPRING_RABBITMQ_USERNAME=guest
SPRING_RABBITMQ_PASSWORD=guest

# Application
SERVER_PORT=8081
SPRING_APPLICATION_NAME=order-service
```

### Spring Profiles

- **default**: Development environment (local Docker containers)
- **docker**: Container environment (using Docker DNS)

Configured in `application.yml` in each service.

## 🚀 Deployment

### Docker Deployment (Recommended)

```bash
# Navigate to project root
cd real-time-order-tracking

# Start all services in Docker
docker-compose up -d

# Verify services are running
docker-compose ps

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

### Available After Deployment

- **Frontend**: http://localhost:3000
- **Order Service API**: http://localhost:8081
- **Inventory Service API**: http://localhost:8082
- **RabbitMQ Dashboard**: http://localhost:15672 (guest/guest)
- **MongoDB Express**: http://localhost:8888 (admin/admin123)

### Development Mode (Running Services Locally)

If you want to run services on your local machine:

```bash
# Build all services
mvn clean package -DskipTests

# Start MongoDB (Docker)
docker run -d -p 27017:27017 --name mongodb mongo:7.0

# Start RabbitMQ (Docker)
docker run -d -p 5672:5672 -p 15672:15672 --name rabbitmq rabbitmq:3.12-management

# Start Order Service
cd backend/order-service
java -jar target/order-service-1.0.0.jar

# Start Inventory Service (in another terminal)
cd backend/inventory-service
java -jar target/inventory-service-1.0.0.jar

# Start Frontend (in another terminal)
cd frontend
npm install
npm run dev
```

## 📊 Monitoring & Debugging

### Health Checks

- **Order Service**: http://localhost:8081/actuator/health
- **Inventory Service**: http://localhost:8082/actuator/health
- **RabbitMQ Dashboard**: http://localhost:15672 (guest/guest)
- **MongoDB Express**: http://localhost:8888 (admin/admin123)

### View Service Logs

```bash
# View all service logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f order-service
docker-compose logs -f inventory-service
docker-compose logs -f mongodb
docker-compose logs -f rabbitmq

# View logs with timestamps
docker-compose logs --timestamps

# View last 100 lines
docker-compose logs --tail=100
```

### Access Container Shell

```bash
# Access Order Service container
docker-compose exec order-service /bin/sh

# Access MongoDB container
docker-compose exec mongodb mongosh

# Check running processes
docker-compose ps
```

### Debugging Events

1. **RabbitMQ Dashboard**: http://localhost:15672
   - View queues and bindings
   - Monitor message flow

2. **MongoDB Express**: http://localhost:8888
   - View documents
   - Check order and inventory data

3. **Service Logs**:
   ```bash
   docker-compose logs -f order-service | grep -i "event\|error"
   ```

---

## 🎯 Interview Demo Workflow

### Quick Demo (5 minutes)

1. **Start everything**
   ```bash
   docker-compose up -d
   docker-compose ps
   ```

2. **Create an order**
   - Open http://localhost:3000
   - Fill in order form and submit
   - Note: Status shows "PENDING"

3. **Check order processing**
   ```bash
   docker-compose logs -f order-service | grep -i "event\|order"
   ```

4. **Verify in MongoDB**
   - Open http://localhost:8888
   - Browse order-db database

5. **View RabbitMQ queues**
   - Open http://localhost:15672
   - Check "Queues" tab to see event flow

6. **Explain the architecture**
   - Event-driven microservices
   - Saga pattern for distributed transactions
   - Asynchronous communication via RabbitMQ

---

## 🤝 Contributing

Contributions are welcome! To contribute:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines

- Write clean, documented code
- Include tests for new features
- Follow Spring Boot best practices
- Update documentation

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👤 Author

**Vatsal Chavda**
- GitHub: [@vatsalchavda](https://github.com/vatsalchavda)
- Email: vatsalchavda2@gmail.com
- LinkedIn: [LinkedIn Profile](https://linkedin.com/in/vatsalchavda)

---

## 🙏 Acknowledgments

- Spring Boot Team for the excellent framework
- RabbitMQ for the reliable message broker
- MongoDB for the flexible database
- React community for the powerful UI framework
- The open-source community for inspiration

---

## 📞 Support

For questions or issues:

- Open an issue on [GitHub](https://github.com/vatsalchavda/real-time-order-tracking/issues)
- Check [LOCAL_DEVELOPMENT_GUIDE.md](LOCAL_DEVELOPMENT_GUIDE.md) for detailed setup help
- Review [INTERVIEW_WALKTHROUGH.md](docs/INTERVIEW_WALKTHROUGH.md) for presentation guidance

---

⭐ **Star this repository if you find it helpful!**

Built with ❤️ using Spring Boot, React, MongoDB, and RabbitMQ