package com.demoproject.gatewayservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping("/fallback/tasks")
    public ResponseEntity<String> taskServiceFallback() {
        return ResponseEntity.ok("Task Service is currently unavailable. Please try again later.");
    }
}

