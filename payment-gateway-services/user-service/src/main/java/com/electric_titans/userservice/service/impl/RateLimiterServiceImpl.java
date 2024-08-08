package com.electric_titans.userservice.service.impl;

import com.electric_titans.userservice.service.RateLimiterService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
public class RateLimiterServiceImpl implements RateLimiterService {

    private final Bucket bucket;

    public RateLimiterServiceImpl() {
        Bandwidth limit = Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1)));
        this.bucket = Bucket4j.builder().addLimit(limit).build();
    }

    @Override
    public boolean isRateLimited() {
        log.debug("isRateLimited()");
        return bucket.tryConsume(1);
    }
}
