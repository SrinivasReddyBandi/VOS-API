package com.vos.service

import com.vos.model.dto.VendorRequestDto
import com.vos.model.dto.VendorResponseDto

interface VendorRequestService {
    VendorResponseDto createVendorRequest(VendorRequestDto requestDto)
    VendorResponseDto getVendorRequest(Long id)
}

