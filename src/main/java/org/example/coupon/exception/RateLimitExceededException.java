package org.example.coupon.exception;

public class RateLimitExceededException extends RuntimeException {

    private final CustomExceptionMessage message;

    public RateLimitExceededException(CustomExceptionMessage message) {
        super(message.getMessage());
        this.message = message;
    }
}
