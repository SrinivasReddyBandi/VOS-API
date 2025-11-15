package com.vos.controller

import com.vos.enums.FileType
import com.vos.model.dto.ApiResponse
import com.vos.model.dto.OnboardingFormDto
import com.vos.model.dto.VendorFileDto
import com.vos.service.VendorOnboardingService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/vendors")
class VendorOnboardingController {
    
    private final VendorOnboardingService vendorOnboardingService
    
    VendorOnboardingController(VendorOnboardingService vendorOnboardingService) {
        this.vendorOnboardingService = vendorOnboardingService
    }
    
    @PutMapping("/{vendorId}/onboarding")
    ResponseEntity<ApiResponse<OnboardingFormDto>> submitOnboardingForm(
            @PathVariable Long vendorId,
            @Valid @RequestBody OnboardingFormDto formDto) {
        OnboardingFormDto response = vendorOnboardingService.submitOnboardingForm(vendorId, formDto)
        ResponseEntity.ok(ApiResponse.success("Onboarding form submitted successfully", response))
    }
    
    @PostMapping("/{vendorId}/files")
    ResponseEntity<ApiResponse<VendorFileDto>> uploadFile(
            @PathVariable Long vendorId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileType") FileType fileType) {
        VendorFileDto response = vendorOnboardingService.uploadFile(vendorId, fileType, file)
        ResponseEntity.ok(ApiResponse.success("File uploaded successfully", response))
    }
}

