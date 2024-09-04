package org.example.coupon.service.coupon;

import lombok.RequiredArgsConstructor;
import org.example.coupon.domain.coupon.CouponInventory;
import org.example.coupon.domain.coupon.CouponIssuance;
import org.example.coupon.repository.coupon.CouponInventoryRepository;
import org.example.coupon.repository.coupon.CouponIssuanceRepository;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IssuanceService {
    private final CouponInventoryRepository couponInventoryRepository;
    private final CouponIssuanceRepository couponIssuanceRepository;

    @Cacheable(value = "couponInventoryCache", key = "#couponId")
    @Transactional(readOnly = true)
    public Long getCouponInventory(Long couponId) {
        CouponInventory inventory = couponInventoryRepository.findByCouponId(couponId);
        return inventory != null ? inventory.getAmount() : 0L;
    }

    // 중복 요청 여부를 캐시로 체크
    @Cacheable(value = "duplicateCheckCache", key = "#userId + ':' + #couponId")
    public boolean isDuplicateRequest(Long userId, Long couponId) {
        // 캐시에 저장된 정보가 없으면 중복이 아님을 의미
        return false;
    }

    @CachePut(value = "couponInventoryCache", key = "#couponId")
    public Long updateCouponInventory(Long couponId, Long newInventory) {
        return newInventory;
    }

    // 중복 요청 방지를 위해 캐시에 발급 기록 저장
    @CachePut(value = "duplicateCheckCache", key = "#userId + ':' + #couponId")
    public boolean saveDuplicateCheck(Long userId, Long couponId) {
        return true;
    }

    @Transactional
    public void saveIssuancesToDatabase(List<CouponIssuance> issuanceBatch) {
        if (!issuanceBatch.isEmpty()) {
            couponIssuanceRepository.saveAll(issuanceBatch);
        }
    }
}
