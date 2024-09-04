package org.example.coupon.domain.coupon;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponId;

    @Column(nullable = false, unique = true)
    private String name;

    private Long discountPrice;

    private Float discountPercent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Builder
    public Coupon(String name, Long discountPrice, Float discountPercent) {
        this.name = name;
        this.discountPrice = discountPrice;
        this.discountPercent = discountPercent;
        this.discountType = setDiscountType(discountPrice, discountPercent);
    }

    private DiscountType setDiscountType(Long discountPrice, Float discountPercent) {
        if (discountPrice == null && discountPercent == null) {
            throw new IllegalArgumentException("discountPrice or discountPercent must be set");
        }
        else if (discountPrice == null) {
            return DiscountType.PERCENT;
        }
        return DiscountType.AMOUNT;
    }
}