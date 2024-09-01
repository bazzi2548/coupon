package org.example.coupon.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheType {
    INVENTORY("couponInventoryCache", 5 * 60, 10000),
    RATE_LIMIT("rateLimitCache", 10, 10000),
    DUPLICATE("memberDuplicateCache", 5 * 60, 10000);


    private final String cacheName;
    private final int expiredAfterWrite;
    private final int maximumSize;
}
