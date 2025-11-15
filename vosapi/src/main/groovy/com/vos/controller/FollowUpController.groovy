package com.vos.controller

import com.vos.model.dto.*
import com.vos.service.FollowUpService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1")
class FollowUpController {
    
    private final FollowUpService followUpService
    
    FollowUpController(FollowUpService followUpService) {
        this.followUpService = followUpService
    }
    
    @PostMapping("/vendors/{vendorId}/follow-ups")
    @PreAuthorize("hasRole('PROCUREMENT')")
    ResponseEntity<ApiResponse<FollowUpDto>> createFollowUp(
            @PathVariable Long vendorId,
            @Valid @RequestBody FollowUpRequestDto requestDto) {
        FollowUpDto response = followUpService.createFollowUp(vendorId, requestDto)
        ResponseEntity.ok(ApiResponse.success("Follow-up created successfully", response))
    }
    
    @GetMapping("/vendors/{vendorId}/follow-ups")
    @PreAuthorize("hasRole('PROCUREMENT')")
    ResponseEntity<ApiResponse<List<FollowUpDto>>> getVendorFollowUps(@PathVariable Long vendorId) {
        List<FollowUpDto> response = followUpService.getVendorFollowUps(vendorId)
        ResponseEntity.ok(ApiResponse.success(response))
    }
    
    @PatchMapping("/follow-ups/{followUpId}")
    @PreAuthorize("hasRole('PROCUREMENT')")
    ResponseEntity<ApiResponse<FollowUpDto>> updateFollowUp(
            @PathVariable Long followUpId,
            @Valid @RequestBody FollowUpUpdateDto updateDto) {
        FollowUpDto response = followUpService.updateFollowUp(followUpId, updateDto)
        ResponseEntity.ok(ApiResponse.success("Follow-up updated successfully", response))
    }
}

