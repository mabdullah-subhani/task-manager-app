package com.demoproject.gatewayservice.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                // Route for task-service
                .route("task-service", r -> r.path("/tasks/**")
                        .filters(f -> f.stripPrefix(0) // remove /tasks prefix
                                .circuitBreaker(c -> c
                                        .setName("taskServiceCB")
                                        .setFallbackUri("forward:/fallback/tasks")))
                        .uri("lb://TASK-SERVICE"))

                // Route for auth-service
                .route("auth-service", r -> r.path("/auth/**")
                        .uri("lb://AUTH-SERVICE"))

                .build();
    }
}
