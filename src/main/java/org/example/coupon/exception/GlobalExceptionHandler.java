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
}
