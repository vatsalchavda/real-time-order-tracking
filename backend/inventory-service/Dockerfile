# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the project files
COPY . .

# Build only the inventory-service module (skip tests for faster builds locally)
# Use -pl and -am so Maven builds the module and its required dependencies from the multi-module reactor
RUN mvn -pl inventory-service -am clean package -DskipTests -Dmaven.repo.local=/app/.m2/repository

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built jar
COPY --from=build /app/inventory-service/target/*.jar app.jar

# Expose port (mapped in docker-compose as 8082)
EXPOSE 8082

# Run the application with configurable port
CMD ["java", "-Dserver.port=${PORT:-8082}", "-jar", "app.jar"]
