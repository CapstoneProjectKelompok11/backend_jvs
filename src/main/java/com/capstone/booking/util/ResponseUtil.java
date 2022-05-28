package com.capstone.booking.util;

import com.capstone.booking.constant.AppConstant;
import com.capstone.booking.domain.common.ApiResponse;
import com.capstone.booking.domain.common.ApiResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public class ResponseUtil {

    private ResponseUtil() {}

    public static <T> ResponseEntity<Object> build(AppConstant.ResponseCode responseCode, T data, HttpStatus httpStatus) {
        return new ResponseEntity<>(buildBody(responseCode, data) ,httpStatus);
    }

    private static <T> ApiResponse<T> buildBody(AppConstant.ResponseCode responseCode, T data) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .data(data)
                .status(ApiResponseStatus.builder()
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .build())
                .build();
    }

}
