package org.example.coupon.controller;

import lombok.RequiredArgsConstructor;
import org.example.coupon.dto.request.IssuanceCouponRequest;
import org.example.coupon.service.RateLimitService;
import org.example.coupon.service.coupon.CouponService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;
    private final RateLimitService rateLimitService;

    @PostMapping("/issuance")
    public String issuanceCoupon(@RequestBody IssuanceCouponRequest request) {
        if (rateLimitService.isRateLimitExceeded(request.userId())) {
            return "요청 초과";
        }

        return couponService.requestCouponIssuance(request);
    }
}
