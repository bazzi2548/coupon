package org.example.coupon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private static final int REQUEST_LIMIT = 5;

    private final CacheManager cacheManager;

    public boolean isRateLimitExceeded(Long userId) {
        Cache cache = cacheManager.getCache("rateLimitCache");
        AtomicInteger requestCount = cache.get(userId, AtomicInteger.class);

        if (requestCount == null) {
            requestCount = new AtomicInteger(0);
            cache.put(userId, requestCount);
        }

        int updatedCount = requestCount.incrementAndGet();

        return updatedCount > REQUEST_LIMIT;
    }
}
