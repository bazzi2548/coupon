package org.example.coupon.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CustomExceptionMessage {

    RATE_LIMIT("비정상적인 요청 입니다. 나중에 다시 시도해주세요.");

    private final String message;
}
