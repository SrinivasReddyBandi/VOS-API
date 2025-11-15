package com.vos.service

import com.vos.model.dto.VendorSessionDto
import com.vos.model.dto.OtpVerifyDto

interface VendorSessionService {
    VendorSessionDto getSessionByToken(String token)
    void sendOtp(String token)
    VendorSessionDto verifyOtp(String token, OtpVerifyDto otpVerifyDto)
}

