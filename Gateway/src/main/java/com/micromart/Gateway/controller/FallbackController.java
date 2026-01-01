package com.micromart.Gateway.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/users")
    public Mono<String> userServiceFallback() {
        return Mono.just("⚠️ Users Service is taking too long or is down. Please try again later.");
    }
}