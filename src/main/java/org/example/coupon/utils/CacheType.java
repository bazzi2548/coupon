package org.example.coupon.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheType {
    INVENTORY("couponInventoryCache", 5 * 60, 1000),
    RATE_LIMIT("rateLimitCache", 10, 1000000),
    DUPLICATE("duplicateCheckCache", 5 * 60, 100000),
    ISSUE_TICKET("issueTicketCache", 60, 100000);

    private final String cacheName;
    private final int expiredAfterWrite;
    private final int maximumSize;
}
