package org.example.coupon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.coupon.dto.request.IssuanceCouponRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {

    private final CacheService cacheService;
    private final BatchService batchService;
    private final ConcurrentLinkedQueue<IssuanceCouponRequest> queue = new ConcurrentLinkedQueue<>();

    public String requestCouponIssuance(IssuanceCouponRequest request) {

        Long currentInventory = cacheService.getCouponInventory(request.couponId());
        if (cacheService.isDuplicateRequest(request.userId(), request.couponId())) {
            return "Duplicate request detected. Coupon already issued.";
        }

        if (currentInventory != null && currentInventory > 0) {
            queue.add(request);
            return "Your request has been added to the queue.";
        } else {
            return "Sorry, the coupon is no longer available.";
        }
    }

    @Scheduled(fixedRate = 1000)
    public void processQueue() {
        log.info("스케쥴러 실행");
        log.info("queue.size = {}", queue.size());
        if(!queue.isEmpty()) {
            log.info("시작 = {}", System.currentTimeMillis());
        }
        while (!queue.isEmpty()) {
            var request = queue.poll();
            Long amount = cacheService.getCouponInventory(request.couponId());
            var issueMembers = cacheService.getIssueMembers(request.couponId());
            if (amount > issueMembers.size()) {
                cacheService.addIssueMember(request.userId(), request.couponId(), issueMembers);
                cacheService.saveDuplicateCheck(request.userId(), request.couponId());
            }

            issueMembers = cacheService.getIssueMembers(request.couponId());
            if (issueMembers.size() == amount) {
                batchService.saveIssuancesToDatabase(request.couponId(), issueMembers);
                cacheService.updateCouponInventory(request.couponId(), issueMembers.size() - amount);
            }
        }
    }
}
