package org.example.coupon.domain.coupon;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes =
@Index(name = "idx_coupon_id", columnList = "coupon_id"))
public class CouponInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long couponId;

    @Column(nullable = false)
    private Long totalAmount;

    @Setter
    @Column(nullable = false)
    private Long remainAmount;

}
