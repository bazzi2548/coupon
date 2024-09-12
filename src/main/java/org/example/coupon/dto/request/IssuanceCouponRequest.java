package org.example.coupon.dto.request;

import jakarta.validation.constraints.NotBlank;

public record IssuanceCouponRequest(
        @NotBlank Long memberId,
        @NotBlank Long couponId
) implements Comparable<IssuanceCouponRequest>, ForMemberRequest {

    @Override
    public int compareTo(IssuanceCouponRequest o) {
        return this.couponId.compareTo(o.couponId);
    }
}
