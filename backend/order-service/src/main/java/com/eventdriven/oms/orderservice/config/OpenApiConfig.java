package com.eventdriven.oms.orderservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger Configuration
 * 
 * This configuration class sets up Swagger UI for API documentation.
 * Swagger provides interactive API documentation that allows developers
 * to test endpoints directly from the browser.
 * 
 * Key Concepts:
 * - @Configuration: Marks this as a Spring configuration class
 * - @Bean: Creates a Spring-managed bean
 * - OpenAPI: Main configuration object for API documentation
 * 
 * Access Swagger UI at: http://localhost:8081/swagger-ui.html
 * Access OpenAPI JSON at: http://localhost:8081/v3/api-docs
 * 
 * Author: Vatsal Chavda <vatsalchavda2@gmail.com>
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8081}")
    private String serverPort;

    /**
     * Configure OpenAPI documentation
     * 
     * This method creates the main OpenAPI configuration with:
     * - API metadata (title, description, version)
     * - Contact information
     * - License information
     * - Server URLs (local and production)
     * 
     * @return OpenAPI configuration object
     */
    @Bean
    public OpenAPI orderServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Order Service API")
                        .description("RESTful API for managing orders in an event-driven microservices architecture. " +
                                "This service handles order creation, retrieval, updates, and deletion, " +
                                "while publishing events to RabbitMQ for downstream processing.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Vatsal Chavda")
                                .email("vatsalchavda2@gmail.com")
                                .url("https://github.com/vatsalchavda"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server"),
                        new Server()
                                .url("https://real-time-order-tracking-production.up.railway.app")
                                .description("Production Server (Railway)")
                ));
    }
}

// Made with Bob
