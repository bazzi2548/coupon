package org.example.coupon.repository.coupon;

import org.example.coupon.domain.coupon.CouponInventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponInventoryRepository extends JpaRepository<CouponInventory, Long> {

    CouponInventory findByCouponId(Long couponId);

}
