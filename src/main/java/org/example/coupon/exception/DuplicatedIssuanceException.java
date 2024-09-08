package org.example.coupon.exception;

public class DuplicatedIssuanceException extends RuntimeException {

    private final CustomExceptionMessage message;

    public DuplicatedIssuanceException(CustomExceptionMessage message) {
        super(message.getMessage());
        this.message = message;
    }
}
