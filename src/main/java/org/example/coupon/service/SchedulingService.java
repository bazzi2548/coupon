package org.example.coupon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.example.coupon.service.CouponService.queue;


@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulingService {

    private final CacheService cacheService;
    private final RedisService redisService;
    private final BatchService batchService;

    @Scheduled(fixedRate = 500)
    public void processQueue() {
        log.info("queue.size() = {}", queue.size());
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

    @Async
    @Scheduled(fixedRate = 1000)
    public void processBatch() {
        List<Long> couponIdKeys = cacheService.getCouponIdKeys();
        couponIdKeys.forEach(couponId -> {
            Long amount = cacheService.getCouponInventory(couponId);
            if (amount >= 0) {
                Set<Long> issueMembers = cacheService.getIssueMembers(couponId);
                long start = System.currentTimeMillis();
                Long size = redisService.luaScriptSetCoupon(couponId, amount, issueMembers);

                if (Objects.equals(size, amount)) {
                    cacheService.updateCouponInventory(couponId, -1L);
                }

                log.info("redis Latency = {} ms", System.currentTimeMillis() - start);
            }
        });
    }

    @Async
    @Scheduled(fixedRate = 5000)
    public void processRDBBatch() {
        List<Long> couponIdKeys = cacheService.getCouponIdKeys();
        couponIdKeys.forEach(couponId -> {
            if (cacheService.getCouponInventory(couponId) == -1L) {
                cacheService.updateCouponInventory(couponId, -2L);
                Set<Object> issueMembers = redisService.getIssueMembers(couponId);
                long start = System.currentTimeMillis();
                batchService.saveIssuancesToDatabase(couponId, issueMembers);

                log.info("rdb batch Latency = {} ms", System.currentTimeMillis() - start);
            }
        });
    }
}
