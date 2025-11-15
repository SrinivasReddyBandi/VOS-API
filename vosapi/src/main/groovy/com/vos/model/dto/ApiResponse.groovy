package com.vos.model.dto

import java.time.LocalDateTime

class ApiResponse<T> {
    boolean success
    String message
    T data
    LocalDateTime timestamp
    
    ApiResponse() {
        this.timestamp = LocalDateTime.now()
    }
    
    ApiResponse(boolean success, String message, T data) {
        this.success = success
        this.message = message
        this.data = data
        this.timestamp = LocalDateTime.now()
    }
    
    static <T> ApiResponse<T> success(T data) {
        new ApiResponse<>(true, "Success", data)
    }
    
    static <T> ApiResponse<T> success(String message, T data) {
        new ApiResponse<>(true, message, data)
    }
    
    static <T> ApiResponse<T> error(String message) {
        new ApiResponse<>(false, message, null)
    }
}

