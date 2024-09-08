package org.example.coupon.service.coupon;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.coupon.domain.coupon.CouponIssuance;
import org.example.coupon.dto.request.IssuanceCouponRequest;
import org.example.coupon.exception.DuplicatedIssuanceException;
import org.example.coupon.exception.ExhaustionException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.example.coupon.exception.CustomExceptionMessage.DUPLICATE_ISSUANCE;
import static org.example.coupon.exception.CustomExceptionMessage.EXHAUSTION;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {

    private final IssuanceService issuanceService;
    private final ConcurrentLinkedQueue<IssuanceCouponRequest> queue = new ConcurrentLinkedQueue<>();

    public String requestCouponIssuance(IssuanceCouponRequest request) {

        Long currentInventory = issuanceService.getCouponInventory(request.couponId());
        if (issuanceService.isDuplicateRequest(request.userId(), request.couponId())) {
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
        List<CouponIssuance> issuanceBatch = new ArrayList<>();

        while (!queue.isEmpty()) {
            IssuanceCouponRequest request = queue.poll();
            if (request != null) {
                Long amount = issuanceService.getCouponInventory(request.couponId());

                if (amount != null && amount > 0) {
                    issuanceBatch.add(new CouponIssuance(request.couponId(), request.userId()));
                    issuanceService.updateCouponInventory(request.couponId(), amount - 1);
                    issuanceService.saveDuplicateCheck(request.userId(), request.couponId());
                }
            }
        }
        issuanceService.saveIssuancesToDatabase(issuanceBatch);
    }
}
