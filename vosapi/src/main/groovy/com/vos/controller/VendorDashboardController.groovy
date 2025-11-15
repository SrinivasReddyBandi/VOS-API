package com.vos.controller

import com.vos.enums.VendorStatus
import com.vos.model.dto.*
import com.vos.service.VendorDashboardService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/vendors")
class VendorDashboardController {
    
    private final VendorDashboardService vendorDashboardService
    
    VendorDashboardController(VendorDashboardService vendorDashboardService) {
        this.vendorDashboardService = vendorDashboardService
    }
    
    @GetMapping
    @PreAuthorize("hasRole('PROCUREMENT')")
    ResponseEntity<ApiResponse<PageResponse<VendorResponseDto>>> getVendors(
            @RequestParam(required = false) VendorStatus status,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<VendorResponseDto> response = vendorDashboardService.getVendors(
            status, name, category, email, page, size)
        ResponseEntity.ok(ApiResponse.success(response))
    }
    
    @GetMapping("/{vendorId}")
    @PreAuthorize("hasRole('PROCUREMENT')")
    ResponseEntity<ApiResponse<VendorDetailDto>> getVendorDetail(@PathVariable Long vendorId) {
        VendorDetailDto response = vendorDashboardService.getVendorDetail(vendorId)
        ResponseEntity.ok(ApiResponse.success(response))
    }
    
    @GetMapping("/{vendorId}/activity-log")
    @PreAuthorize("hasRole('PROCUREMENT')")
    ResponseEntity<ApiResponse<List<ActivityLogDto>>> getVendorActivityLog(@PathVariable Long vendorId) {
        List<ActivityLogDto> response = vendorDashboardService.getVendorActivityLog(vendorId)
        ResponseEntity.ok(ApiResponse.success(response))
    }
    
    @GetMapping("/metrics")
    @PreAuthorize("hasRole('PROCUREMENT')")
    ResponseEntity<ApiResponse<VendorMetricsDto>> getMetrics() {
        VendorMetricsDto response = vendorDashboardService.getMetrics()
        ResponseEntity.ok(ApiResponse.success(response))
    }
}

