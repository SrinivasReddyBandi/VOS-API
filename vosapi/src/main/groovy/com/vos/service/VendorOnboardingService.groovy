package com.vos.service

import com.vos.enums.FileType
import com.vos.model.dto.OnboardingFormDto
import com.vos.model.dto.VendorFileDto
import org.springframework.web.multipart.MultipartFile

interface VendorOnboardingService {
    OnboardingFormDto submitOnboardingForm(Long vendorId, OnboardingFormDto formDto)
    VendorFileDto uploadFile(Long vendorId, FileType fileType, MultipartFile file)
}