package com.vos.service

import com.vos.enums.VendorStatus
import com.vos.model.dto.*
import org.springframework.core.io.Resource

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

    List<VendorFileDto> getVendorFiles(Long vendorId)
    VendorFileDto getVendorFile(Long vendorId, Long fileId)
    // returns a Spring Resource for download
    Resource downloadFile(Long vendorId, Long fileId)
    // re-run validation on an existing file
    VendorFileDto revalidateFile(Long vendorId, Long fileId)
}
