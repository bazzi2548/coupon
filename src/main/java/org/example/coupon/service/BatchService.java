package org.example.coupon.service;

import lombok.RequiredArgsConstructor;
import org.example.coupon.domain.coupon.CouponInventory;
import org.example.coupon.dto.CouponIssuanceDTO;
import org.example.coupon.repository.coupon.CouponBulkRepository;
import org.example.coupon.repository.coupon.CouponInventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BatchService {

    private final CouponBulkRepository repository;
    private final CouponInventoryRepository inventoryRepository;

    @Transactional
    public void saveIssuancesToDatabase(Long couponId, Set<Object> issuanceBatch) {

        List<CouponIssuanceDTO> list = issuanceBatch.stream()
                .map(i -> new CouponIssuanceDTO(couponId, Long.parseLong(String.valueOf(i))))
                .toList();

        if (!issuanceBatch.isEmpty()) {
            repository.saveAll(list);
            CouponInventory couponInfo = inventoryRepository.findByCouponId(couponId);
            couponInfo.setRemainAmount(0L);
        }
    }
}
