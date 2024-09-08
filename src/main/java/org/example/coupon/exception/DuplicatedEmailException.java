package org.example.coupon.exception;

public class DuplicatedEmailException extends RuntimeException {

    private final CustomExceptionMessage message;

    public DuplicatedEmailException(final CustomExceptionMessage message) {
        super(message.getMessage());
        this.message = message;
    }
}
