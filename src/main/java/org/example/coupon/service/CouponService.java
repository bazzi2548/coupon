package org.example.coupon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.coupon.dto.request.IssuanceCouponRequest;
import org.example.coupon.exception.DuplicatedIssuanceException;
import org.example.coupon.exception.ExhaustionException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentLinkedQueue;

import static org.example.coupon.exception.CustomExceptionMessage.DUPLICATE_ISSUANCE;
import static org.example.coupon.exception.CustomExceptionMessage.EXHAUSTION;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {

    private final CacheService cacheService;
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
                cacheService.addIssueMember(request.memberId(), request.couponId(), issueMembers);
                cacheService.saveDuplicateCheck(request.memberId(), request.couponId());
            }

            issueMembers = cacheService.getIssueMembers(request.couponId());
            if (issueMembers.size() == amount) {
                batchService.saveIssuancesToDatabase(request.couponId(), issueMembers);
                cacheService.updateCouponInventory(request.couponId(), issueMembers.size() - amount);
            }
        }
    }
}
