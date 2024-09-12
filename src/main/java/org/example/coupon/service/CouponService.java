package org.example.coupon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.coupon.dto.request.IssuanceCouponRequest;
import org.example.coupon.exception.DuplicatedIssuanceException;
import org.example.coupon.exception.ExhaustionException;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentLinkedQueue;

import static org.example.coupon.exception.CustomExceptionMessage.DUPLICATE_ISSUANCE;
import static org.example.coupon.exception.CustomExceptionMessage.EXHAUSTION;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponService {

    private final CacheService cacheService;

    protected static final ConcurrentLinkedQueue<IssuanceCouponRequest> queue = new ConcurrentLinkedQueue<>();

    public String requestCouponIssuance(IssuanceCouponRequest request) {

        Long currentInventory = cacheService.getCouponInventory(request.couponId());
        if (cacheService.isDuplicateRequest(request.memberId(), request.couponId())) {
            throw new DuplicatedIssuanceException(DUPLICATE_ISSUANCE);
        }

        if (currentInventory != null && currentInventory > 0) {
            queue.add(request);
            return "Your request has been added to the queue.";
        } else {
            throw new ExhaustionException(EXHAUSTION);
        }
    }
}
