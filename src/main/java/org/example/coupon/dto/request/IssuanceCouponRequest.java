package org.example.coupon.dto.request;

import jakarta.validation.constraints.NotBlank;

public record IssuanceCouponRequest(
        @NotBlank Long userId,
        @NotBlank Long couponId
) implements Comparable<IssuanceCouponRequest> {

    @Override
    public int compareTo(IssuanceCouponRequest o) {
        return this.couponId.compareTo(o.couponId);
    }
}
