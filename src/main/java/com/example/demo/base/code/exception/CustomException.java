package com.example.demo.base.code.exception;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.status.ErrorStatus;
import org.springframework.http.ResponseEntity;

public class CustomException extends RuntimeException {

    private final ErrorStatus errorStatus;
    private final Object data;

    public CustomException(ErrorStatus errorStatus) {
        super(errorStatus.getMessage());
        this.errorStatus = errorStatus;
        this.data = null;
    }

    public CustomException(ErrorStatus errorStatus, Object data) {
        super(errorStatus.getMessage());
        this.errorStatus = errorStatus;
        this.data = data;
    }

    public ErrorStatus getErrorStatus() {
        return errorStatus;
    }

    public Object getData() {
        return data;
    }

    public static <T> ResponseEntity<ApiResponse<T>> createErrorResponse(ErrorStatus errorStatus, T data) {
        return ResponseEntity
                .status(errorStatus.getHttpStatus())
                .body(ApiResponse.onFailure(errorStatus, data));
    }
}
