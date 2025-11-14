# Multi-stage build for Order Service
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# Copy parent pom and common module first (for caching)
COPY pom.xml .
COPY common ./common

# Copy order-service
COPY order-service/pom.xml ./order-service/
COPY order-service/src ./order-service/src

# Build the application
WORKDIR /build/order-service
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built jar
COPY --from=build /build/order-service/target/*.jar app.jar

# Expose port
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]