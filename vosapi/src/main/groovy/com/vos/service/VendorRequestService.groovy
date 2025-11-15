package com.vos.service

import com.vos.model.dto.VendorRequestDto
import com.vos.model.dto.VendorResponseDto
import com.vos.model.dto.OnboardingFormDto

interface VendorRequestService {
    VendorResponseDto createVendorRequest(VendorRequestDto requestDto)
    VendorResponseDto getVendorRequest(Long id)
    OnboardingFormDto getOnboardingFormByVendorId(Long vendorId)
}
