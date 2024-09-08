package org.example.coupon.exception;

public class ExhaustionException extends RuntimeException {

    private CustomExceptionMessage message;

    public ExhaustionException(CustomExceptionMessage message) {
        super(message.getMessage());
        this.message = message;
    }
}
