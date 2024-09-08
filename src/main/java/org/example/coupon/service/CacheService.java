package org.example.coupon.service;

import lombok.RequiredArgsConstructor;
import org.example.coupon.domain.coupon.CouponInventory;
import org.example.coupon.repository.coupon.CouponInventoryRepository;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class CacheService {
    private final CouponInventoryRepository couponInventoryRepository;

    @Cacheable(value = "couponInventoryCache", key = "#couponId")
    @Transactional(readOnly = true)
    public Long getCouponInventory(Long couponId) {
        CouponInventory inventory = couponInventoryRepository.findByCouponId(couponId);
        return inventory != null ? inventory.getAmount() : 0L;
    }

    @Cacheable(value = "duplicateCheckCache", key = "#userId + ':' + #couponId")
    public boolean isDuplicateRequest(Long userId, Long couponId) {
        // 캐시에 저장된 정보가 없으면 중복이 아님을 의미
        return false;
    }

    @Cacheable(value = "issueTicketCache", key = "#couponId")
    public Set<Long> getIssueMembers(Long couponId) {
        return ConcurrentHashMap.newKeySet();
    }

    @CachePut(value = "issueTicketCache", key = "#couponId")
    public Set<Long> addIssueMember(Long userId, Long couponId, Set<Long> members) {
        members.add(userId);
        return members;
    }

    @CachePut(value = "couponInventoryCache", key = "#couponId")
    public Long updateCouponInventory(Long couponId, Long newInventory) {
        return newInventory;
    }

    @CachePut(value = "duplicateCheckCache", key = "#userId + ':' + #couponId")
    public boolean saveDuplicateCheck(Long userId, Long couponId) {
        return true;
    }
}
