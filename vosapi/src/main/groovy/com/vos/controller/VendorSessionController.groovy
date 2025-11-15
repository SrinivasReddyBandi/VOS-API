package com.vos.controller

import com.vos.model.dto.ApiResponse
import com.vos.model.dto.OtpRequestDto
import com.vos.model.dto.OtpVerifyDto
import com.vos.model.dto.VendorSessionDto
import com.vos.service.VendorSessionService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/vendor-sessions")
class VendorSessionController {
    
    private final VendorSessionService vendorSessionService
    
    VendorSessionController(VendorSessionService vendorSessionService) {
        this.vendorSessionService = vendorSessionService
    }
    
    @GetMapping("/{token}")
    ResponseEntity<ApiResponse<VendorSessionDto>> getSession(@PathVariable String token) {
        VendorSessionDto session = vendorSessionService.getSessionByToken(token)
        ResponseEntity.ok(ApiResponse.success(session))
    }
    
    @PostMapping("/{token}/otp/send")
    ResponseEntity<ApiResponse<Void>> sendOtp(@PathVariable String token,
                                               @RequestBody(required = false) OtpRequestDto request) {
        vendorSessionService.sendOtp(token)
        ResponseEntity.ok(ApiResponse.success("OTP sent successfully", null))
    }
    
    @PostMapping("/{token}/otp/verify")
    ResponseEntity<ApiResponse<VendorSessionDto>> verifyOtp(
            @PathVariable String token,
            @Valid @RequestBody OtpVerifyDto otpVerifyDto) {
        VendorSessionDto session = vendorSessionService.verifyOtp(token, otpVerifyDto)
        ResponseEntity.ok(ApiResponse.success("OTP verified successfully", session))
    }
}

