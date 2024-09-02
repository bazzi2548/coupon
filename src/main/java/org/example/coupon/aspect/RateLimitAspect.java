package org.example.coupon.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.example.coupon.dto.request.ForMemberRequest;
import org.example.coupon.exception.RateLimitExceededException;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

import static org.example.coupon.exception.CustomExceptionMessage.RATE_LIMIT;

@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private static final int REQUEST_LIMIT = 5;
    private final CacheManager cacheManager;

    @Pointcut("@annotation(org.example.coupon.aspect.RateLimit)")
    public void rateLimitedMethod(){}

    @Around("rateLimitedMethod()")
    public Object isRateLimitExceeded(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        if(!(args[0] instanceof ForMemberRequest)){
            return joinPoint.proceed();
        }
        Long userId = ((ForMemberRequest) args[0]).userId();

        Cache cache = cacheManager.getCache("rateLimitCache");
        AtomicInteger requestCount = cache.get(userId, AtomicInteger.class);

        if (requestCount == null) {
            requestCount = new AtomicInteger(0);
            cache.put(userId, requestCount);
        }

        int updatedCount = requestCount.incrementAndGet();

        if (updatedCount > REQUEST_LIMIT) {
            log.warn("Rate limit exceeded for userId: {}", userId);
            throw new RateLimitExceededException(RATE_LIMIT);
        }

        return joinPoint.proceed();
    }
}
