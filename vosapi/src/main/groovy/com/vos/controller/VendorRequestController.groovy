package com.vos.controller

import com.vos.model.dto.ApiResponse
import com.vos.model.dto.VendorRequestDto
import com.vos.model.dto.VendorResponseDto
import com.vos.service.VendorRequestService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/vendor-requests")
class VendorRequestController {
    
    private final VendorRequestService vendorRequestService
    
    VendorRequestController(VendorRequestService vendorRequestService) {
        this.vendorRequestService = vendorRequestService
    }
    
    @PostMapping
    @PreAuthorize("hasRole('PROCUREMENT')")
    ResponseEntity<ApiResponse<VendorResponseDto>> createVendorRequest(
            @Valid @RequestBody VendorRequestDto requestDto) {
        VendorResponseDto response = vendorRequestService.createVendorRequest(requestDto)
        ResponseEntity.ok(ApiResponse.success("Vendor request created successfully", response))
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PROCUREMENT')")
    ResponseEntity<ApiResponse<VendorResponseDto>> getVendorRequest(@PathVariable Long id) {
        VendorResponseDto response = vendorRequestService.getVendorRequest(id)
        ResponseEntity.ok(ApiResponse.success(response))
    }
}

