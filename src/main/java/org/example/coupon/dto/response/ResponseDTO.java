package org.example.coupon.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class ResponseDTO<T> {

    private final Status status;
    private final T data;
    private final String message;

    public static <T> ResponseDTO getSuccess(T data) {
        return ResponseDTO.builder()
                .status(Status.SUCCESS)
                .data(data)
                .message("요청 성공")
                .build();
    }

    public static ResponseDTO getFailResult(String message) {
        return ResponseDTO.builder()
                .status(Status.FAIL)
                .data(null)
                .message(message)
                .build();
    }

    private enum Status{
        SUCCESS,
        FAIL
    }
}
