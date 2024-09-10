package org.example.coupon.service;

import lombok.RequiredArgsConstructor;
import org.example.coupon.domain.coupon.CouponInventory;
import org.example.coupon.repository.coupon.CouponInventoryRepository;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class CacheService {
    private final CouponInventoryRepository couponInventoryRepository;
    private final CacheManager cacheManager;

    @Cacheable(value = "couponInventoryCache", key = "#couponId")
    @Transactional(readOnly = true)
    public Long getCouponInventory(Long couponId) {
        CouponInventory inventory = couponInventoryRepository.findByCouponId(couponId);
        return inventory != null ? inventory.getAmount() : 0L;
    }

    @Cacheable(value = "duplicateCheckCache", key = "#memberId + ':' + #couponId")
    public boolean isDuplicateRequest(Long memberId, Long couponId) {
        // 캐시에 저장된 정보가 없으면 중복이 아님을 의미
        return false;
    }

    @Cacheable(value = "issueTicketCache", key = "#couponId")
    public Set<Long> getIssueMembers(Long couponId) {
        return ConcurrentHashMap.newKeySet();
    }

    @CachePut(value = "issueTicketCache", key = "#couponId")
    public Set<Long> addIssueMember(Long memberId, Long couponId, Set<Long> members) {
        members.add(memberId);
        return members;
    }

    @CachePut(value = "couponInventoryCache", key = "#couponId")
    public Long updateCouponInventory(Long couponId, Long newInventory) {
        return newInventory;
    }

    @CachePut(value = "duplicateCheckCache", key = "#memberId + ':' + #couponId")
    public boolean saveDuplicateCheck(Long memberId, Long couponId) {
        return true;
    }

    public List<Long> getCouponIdKeys() {
        CaffeineCache couponInventoryCache = (CaffeineCache) cacheManager.getCache("couponInventoryCache");
        return couponInventoryCache.getNativeCache()
                .asMap()
                .keySet()
                .stream()
                .mapToLong(i -> (long) i)
                .boxed()
                .toList();
    }
}
