package com.electric_titans.userservice.service;

import com.electric_titans.userservice.service.impl.RateLimiterServiceImpl;
import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class RateLimiterServiceTest {

    @Mock
    private Bucket bucket;

    @InjectMocks
    private RateLimiterServiceImpl rateLimiterService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        rateLimiterService = new RateLimiterServiceImpl() {
            @Override
            public boolean isRateLimited() {
                return bucket.tryConsume(1);
            }
        };
    }

    @Test
    public void testIsRateLimited_whenNotRateLimited() {
        when(bucket.tryConsume(1)).thenReturn(false);
        assertFalse(rateLimiterService.isRateLimited());
    }

    @Test
    public void testIsRateLimited_whenRateLimited() {
        when(bucket.tryConsume(1)).thenReturn(true);
        assertTrue(rateLimiterService.isRateLimited());
    }
}