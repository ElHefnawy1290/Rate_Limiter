package com.example.Rate_Limiter.Controller;

import com.example.Rate_Limiter.services.RateLimitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisController {
    private final RateLimitService rateLimitService;
    public RedisController(RateLimitService rateLimitService){
        this.rateLimitService = rateLimitService;
    }

    @GetMapping("/api/resource")
    public ResponseEntity<String> getResource(@RequestHeader(value = "X-User-Id", defaultValue = "test-user") String userId){
        boolean allowed = rateLimitService.allowRequest(userId, 1);
        if(allowed)
            return ResponseEntity.ok("Allowed");
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too Many Requests");
    }
}
