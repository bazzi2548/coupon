package org.example.coupon.exception;

public class DuplicatedNickNameException extends RuntimeException {

    private final CustomExceptionMessage message;

    public DuplicatedNickNameException(CustomExceptionMessage message) {
        super(message.getMessage());
        this.message = message;
    }
}
