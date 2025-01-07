package com.example.demo.base.code.exception;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.status.ErrorStatus;
import org.springframework.http.ResponseEntity;

public class CustomException extends RuntimeException {

    public CustomException(ErrorStatus errorStatus) {
        super(errorStatus.getMessage());
    }
    public static <T> ResponseEntity<ApiResponse<T>> createErrorResponse(ErrorStatus errorStatus, T data) {
        return ResponseEntity.badRequest().body(ApiResponse.onFailure(errorStatus, data));
    }
}