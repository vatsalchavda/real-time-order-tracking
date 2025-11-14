# Multi-stage build for Order Service
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# Copy parent pom and common module from parent directory
COPY ../pom.xml ./pom.xml
COPY ../common ./common

# Copy order-service (current directory)
COPY pom.xml ./order-service/pom.xml
COPY src ./order-service/src

# Build the application
WORKDIR /build
RUN mvn clean install -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built jar
COPY --from=build /build/order-service/target/*.jar app.jar

# Expose port (Railway uses PORT env variable)
EXPOSE 8080

# Run the application with dynamic port
ENTRYPOINT ["java", "-Dserver.port=${PORT:-8080}", "-jar", "app.jar"]