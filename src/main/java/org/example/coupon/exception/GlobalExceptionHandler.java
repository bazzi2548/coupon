package org.example.coupon.exception;

import org.example.coupon.dto.response.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    @ExceptionHandler(RateLimitExceededException.class)
    protected ResponseDTO handleRateLimitExceededException(RateLimitExceededException e) {
        return ResponseDTO.getFailResult(e.getMessage());
    }

    @ResponseStatus(HttpStatus.GONE)
    @ExceptionHandler(ExhaustionException.class)
    protected ResponseDTO handleExhaustionException(ExhaustionException e) {
        return ResponseDTO.getFailResult(e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicatedIssuanceException.class)
    protected ResponseDTO handleDuplicatedIssuanceException(DuplicatedIssuanceException e) {
        return ResponseDTO.getFailResult(e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicatedEmailException.class)
    protected ResponseDTO handleDuplicatedEmailException(DuplicatedEmailException e) {
        return ResponseDTO.getFailResult(e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicatedNickNameException.class)
    protected ResponseDTO handleDuplicatedNicknameException(DuplicatedNickNameException e) {
        return ResponseDTO.getFailResult(e.getMessage());
    }
}
