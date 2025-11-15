package com.vos.model.dto

import com.vos.enums.VendorStatus
import java.time.LocalDateTime

class VendorSessionDto {
    Long vendorId
    String vendorName
    String vendorEmail
    VendorStatus status
    Boolean otpVerified
    LocalDateTime tokenExpiry
}

