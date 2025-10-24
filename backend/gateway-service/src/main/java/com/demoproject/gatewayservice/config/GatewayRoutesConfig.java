package com.demoproject.gatewayservice.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                // Task Service Route
                .route("task-service", r -> r.path("/tasks/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("taskServiceCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/tasks")))
                        .uri("lb://TASK-SERVICE"))

                // Auth Service Route
                .route("auth-service", r -> r.path("/auth/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("authServiceCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/auth")))
                        .uri("lb://AUTH-SERVICE"))

                .build();
    }
}
