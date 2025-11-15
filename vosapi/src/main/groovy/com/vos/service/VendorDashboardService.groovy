package com.vos.service

import com.vos.enums.VendorStatus
import com.vos.model.dto.*

interface VendorDashboardService {
    PageResponse<VendorResponseDto> getVendors(
        VendorStatus status,
        String name,
        String category,
        String email,
        int page,
        int size
    )
    
    VendorDetailDto getVendorDetail(Long vendorId)
    List<ActivityLogDto> getVendorActivityLog(Long vendorId)
    VendorMetricsDto getMetrics()
}

