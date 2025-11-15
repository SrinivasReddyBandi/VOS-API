package com.vos.exception

import com.vos.model.dto.ApiResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler)
    
    @ExceptionHandler(ResourceNotFoundException)
    ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
        logger.warn("Resource not found: ${ex.message}")
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ex.message))
    }
    
    @ExceptionHandler(InvalidTokenException)
    ResponseEntity<ApiResponse<Void>> handleInvalidToken(InvalidTokenException ex) {
        logger.warn("Invalid token: ${ex.message}")
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(ex.message))
    }
    
    @ExceptionHandler(InvalidOtpException)
    ResponseEntity<ApiResponse<Void>> handleInvalidOtp(InvalidOtpException ex) {
        logger.warn("Invalid OTP: ${ex.message}")
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(ex.message))
    }
    
    @ExceptionHandler(ValidationException)
    ResponseEntity<ApiResponse<Void>> handleValidation(ValidationException ex) {
        logger.warn("Validation error: ${ex.message}")
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(ex.message))
    }
    
    @ExceptionHandler(MethodArgumentNotValidException)
    ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = [:]
        ex.bindingResult.allErrors.each { error ->
            String fieldName = ((FieldError) error).field
            String errorMessage = error.defaultMessage
            errors[fieldName] = errorMessage
        }
        logger.warn("Validation errors: ${errors}")
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("Validation failed"))
    }
    
    @ExceptionHandler(Exception)
    ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        logger.error("Unexpected error: ${ex.message}", ex)
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("An unexpected error occurred"))
    }
}

