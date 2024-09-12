package org.example.coupon.controller;

import lombok.RequiredArgsConstructor;
import org.example.coupon.dto.request.IssuanceCouponRequest;
import org.example.coupon.service.CouponService;
import org.example.coupon.aspect.RateLimit;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping("/issuance")
    @RateLimit
    public String issuanceCoupon(@RequestBody IssuanceCouponRequest request) {
        return couponService.requestCouponIssuance(request);
    }
}
