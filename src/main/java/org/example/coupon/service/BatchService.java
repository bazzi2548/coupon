package org.example.coupon.service;

import lombok.RequiredArgsConstructor;
import org.example.coupon.domain.coupon.CouponIssuance;
import org.example.coupon.repository.coupon.CouponBulkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BatchService {

    private final CouponBulkRepository repository;

    @Transactional
    public void saveIssuancesToDatabase(Long couponId, Set<Long> issuanceBatch) {

        List<CouponIssuance> list = issuanceBatch.stream()
                .map(i -> new CouponIssuance(couponId, i))
                .toList();

        if (!issuanceBatch.isEmpty()) {
            repository.saveAll(list);
        }
    }
}
