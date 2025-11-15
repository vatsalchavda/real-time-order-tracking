package com.eventdriven.oms.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SPRINGBOOT APPLICATION CLASS - THE ENTRY POINT
 * 
 * @SpringBootApplication is a convenience annotation that combines:
 * 1. @Configuration - Marks this class as a source of bean definitions
 * 2. @EnableAutoConfiguration - Tells Spring Boot to auto-configure based on dependencies
 * 3. @ComponentScan - Scans for components in this package and sub-packages
 * 
 * HOW SPRINGBOOT AUTO-CONFIGURATION WORKS:
 * - Spring Boot looks at your classpath dependencies
 * - If it finds spring-boot-starter-web, it configures a web server (Tomcat by default)
 * - If it finds spring-boot-starter-data-mongodb, it configures MongoDB connection
 * - If it finds spring-cloud-stream, it configures message broker connections
 * 
 * This "convention over configuration" approach reduces boilerplate code significantly.
 * 
 * COMPARISON WITH EXPRESS.JS:
 * In Express, you manually create the server:
 *   const express = require('express');
 *   const app = express();
 *   app.listen(3000);
 * 
 * In Spring Boot, the server is auto-configured. You just run the application.
 */
@SpringBootApplication
public class OrderServiceApplication {

    /**
     * MAIN METHOD - APPLICATION ENTRY POINT
     * 
     * SpringApplication.run() does several things:
     * 1. Creates an ApplicationContext (Spring's IoC container)
     * 2. Scans for @Component, @Service, @Repository, @Controller annotations
     * 3. Creates instances of these classes (beans)
     * 4. Injects dependencies between beans (Dependency Injection)
     * 5. Starts the embedded web server (if spring-boot-starter-web is present)
     * 6. Runs any @PostConstruct methods or CommandLineRunner beans
     * 
     * DEPENDENCY INJECTION (DI) CONCEPT:
     * Instead of creating objects manually (new OrderService()), Spring creates
     * and manages them. You just declare what you need using @Autowired.
     * 
     * Benefits:
     * - Loose coupling (easy to swap implementations)
     * - Easier testing (can inject mocks)
     * - Centralized configuration
     */
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
    
    /**
     * BEAN LIFECYCLE:
     * 1. Spring scans for @Component, @Service, @Repository, @Controller
     * 2. Creates instances (beans) and stores them in ApplicationContext
     * 3. Injects dependencies (@Autowired)
     * 4. Calls @PostConstruct methods (initialization)
     * 5. Bean is ready to use
     * 6. On shutdown, calls @PreDestroy methods (cleanup)
     */
}

