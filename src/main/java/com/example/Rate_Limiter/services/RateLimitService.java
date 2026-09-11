package com.example.Rate_Limiter.services;

import lombok.Builder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;

@Service
public class RateLimitService {
    public final StringRedisTemplate redisTemplate;
    public final DefaultRedisScript<Long> script;

    private static final double capacity = 100.0;
    private static final double rate = 5.0/6000.0;

    public RateLimitService(StringRedisTemplate redisTemplate, DefaultRedisScript<Long> script){
        this.redisTemplate = redisTemplate;
        this.script = script;
    }

    public boolean allowRequest(String userID, int weight){
        String key = "rate_limit:" + userID;
        Long now = Instant.now().toEpochMilli();
        Long result =  redisTemplate.execute(
                script,
                Collections.singletonList(key),
                String.valueOf(capacity),
                String.valueOf(rate),
                String.valueOf(now),
                String.valueOf(weight)
        );
        return result!=null && result == 1L;
    }
}
