package org.example.coupon.repository.coupon;

import org.example.coupon.domain.coupon.CouponIssuance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponIssuanceRepository extends JpaRepository<CouponIssuance, Long> {
}
