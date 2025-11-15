package com.vos.model.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

class OtpVerifyDto {
    
    @NotBlank(message = "OTP is required")
    @Pattern(regexp = "\\d{6}", message = "OTP must be 6 digits")
    String otp
}

