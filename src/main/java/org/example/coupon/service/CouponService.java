package org.example.coupon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.coupon.dto.request.IssuanceCouponRequest;
import org.example.coupon.exception.DuplicatedIssuanceException;
import org.example.coupon.exception.ExhaustionException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.example.coupon.exception.CustomExceptionMessage.DUPLICATE_ISSUANCE;
import static org.example.coupon.exception.CustomExceptionMessage.EXHAUSTION;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {

    private final CacheService cacheService;
    private final RedisService redisService;
    private final BatchService batchService;

    private final ConcurrentLinkedQueue<IssuanceCouponRequest> queue = new ConcurrentLinkedQueue<>();

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

    @Async
    @Scheduled(fixedRate = 1000)
    public void processQueue() {
        while (!queue.isEmpty()) {
            var request = queue.poll();
            Long amount = cacheService.getCouponInventory(request.couponId());
            var issueMembers = cacheService.getIssueMembers(request.couponId());
            if (amount > issueMembers.size()) {
                cacheService.addIssueMember(request.memberId(), request.couponId(), issueMembers);
                cacheService.saveDuplicateCheck(request.memberId(), request.couponId());
            }
        }
    }

    @Scheduled(fixedRate = 1000)
    public void processBatch() {
        List<Long> couponIdKeys = cacheService.getCouponIdKeys();
        couponIdKeys.forEach(couponId -> {
            long start = System.currentTimeMillis();
            Long amount = cacheService.getCouponInventory(couponId);
            Set<Long> issueMembers = cacheService.getIssueMembers(couponId);
            Long size = redisService.luaScriptSetCoupon(couponId, amount, issueMembers);

            if (Objects.equals(size, amount)){
                cacheService.updateCouponInventory(couponId, -1L);
            }

            log.info("처리시간 = {} ms", System.currentTimeMillis() - start);
        });
    }
}
