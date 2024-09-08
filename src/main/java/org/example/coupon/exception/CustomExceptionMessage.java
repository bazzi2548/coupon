package org.example.coupon.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CustomExceptionMessage {

    RATE_LIMIT("너무 많은 요청 입니다. 나중에 다시 시도해주세요."),
    DUPLICATE_EMAIL("이미 존재하는 이메일 입니다."),
    DUPLICATE_NICKNAME("이미 존재하는 닉네임 입니다."),
    DUPLICATE_ISSUANCE("이미 쿠폰을 발급 받았습니다."),
    EXHAUSTION("쿠폰이 모두 소진 되었습니다.");

    private final String message;
}
