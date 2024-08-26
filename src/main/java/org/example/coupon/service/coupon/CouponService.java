package org.example.coupon.service.coupon;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.coupon.domain.coupon.CouponInventory;
import org.example.coupon.domain.coupon.CouponIssuance;
import org.example.coupon.dto.request.IssuanceCouponRequest;
import org.example.coupon.repository.coupon.CouponInventoryRepository;
import org.example.coupon.repository.coupon.CouponIssuanceRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {

    private final Cache<Long, Long> couponInventoryCache;
    private final CouponInventoryRepository couponInventoryRepository;
    private final CouponIssuanceRepository couponIssuanceRepository;
    private final ConcurrentSkipListSet<IssuanceCouponRequest> queue = new ConcurrentSkipListSet<>();

    public String requestCouponIssuance(IssuanceCouponRequest request) {
        Long couponId = request.couponId();
        Long currentInventory = couponInventoryCache.get(couponId, this::loadCouponInventoryFromDB);

        if (currentInventory != null && currentInventory > 0) {
            // 대기열에 추가
            queue.add(request);
            return "Your request has been added to the queue.";
        } else {
            return "Sorry, the coupon is no longer available.";
        }
    }

    // DB에서 쿠폰 재고를 가져오고 캐시에 저장하는 메서드
    @Transactional(readOnly = true)
    protected Long loadCouponInventoryFromDB(Long couponId) {
        CouponInventory inventory = couponInventoryRepository.findByCouponId(couponId);
        return inventory != null ? inventory.getAmount() : 0L;
    }

    @Scheduled(fixedRate = 3000) // 3초 간격으로 대기열 처리
    public void processQueue() {
        log.info("스케쥴러 실행");
        List<CouponIssuance> issuanceBatch = new ArrayList<>();

        while (!queue.isEmpty()) {
            IssuanceCouponRequest request = queue.pollFirst();
            if (request != null) {
                Long currentInventory = couponInventoryCache.getIfPresent(request.couponId());

                // 재고가 남아있다면
                if (currentInventory != null && currentInventory > 0) {
                    // 쿠폰 발급 처리 (DB에 저장할 준비)
                    issuanceBatch.add(new CouponIssuance(request.couponId(), request.userId()));

                    // 캐시에서 재고 감소
                    couponInventoryCache.asMap().computeIfPresent(request.couponId(), (key, value) -> value - 1);
                }
            }
        }

        // 배치 처리로 DB에 저장
        saveIssuancesToDatabase(issuanceBatch);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void saveIssuancesToDatabase(List<CouponIssuance> issuanceBatch) {
        if (!issuanceBatch.isEmpty()) {
            couponIssuanceRepository.saveAll(issuanceBatch);
        }
    }
}
