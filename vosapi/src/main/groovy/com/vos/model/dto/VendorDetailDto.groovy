package com.vos.model.dto

import com.vos.enums.VendorStatus
import java.time.LocalDateTime

class VendorDetailDto {
    Long id
    String name
    String email
    String contactPerson
    String contactNumber
    String category
    String remarks
    VendorStatus status
    LocalDateTime createdAt
    LocalDateTime updatedAt
    OnboardingFormDto onboardingForm
    List<VendorFileDto> files
    List<FollowUpDto> followUps
}

