package com.vos.service.impl

import com.vos.domain.*
import com.vos.enums.VendorStatus
import com.vos.exception.ResourceNotFoundException
import com.vos.model.dto.*
import com.vos.repository.*
import com.vos.service.VendorDashboardService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class VendorDashboardServiceImpl implements VendorDashboardService {
    
    private final VendorRepository vendorRepository
    private final FollowUpRepository followUpRepository
    private final ActivityLogRepository activityLogRepository
    
    VendorDashboardServiceImpl(VendorRepository vendorRepository,
                              FollowUpRepository followUpRepository,
                              ActivityLogRepository activityLogRepository) {
        this.vendorRepository = vendorRepository
        this.followUpRepository = followUpRepository
        this.activityLogRepository = activityLogRepository
    }
    
    @Override
    PageResponse<VendorResponseDto> getVendors(VendorStatus status, String name, 
                                               String category, String email, 
                                               int page, int size) {
        Pageable pageable = PageRequest.of(page, size)
        def vendorPage = vendorRepository.findWithFilters(status, name, category, email, pageable)
        
        List<VendorResponseDto> content = vendorPage.content.collect { toResponseDto(it) }
        
        new PageResponse<>(content, page, size, vendorPage.totalElements)
    }
    
    @Override
    VendorDetailDto getVendorDetail(Long vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
            .orElseThrow { new ResourceNotFoundException("Vendor", vendorId) }
        
        VendorDetailDto dto = new VendorDetailDto()
        dto.id = vendor.id
        dto.name = vendor.name
        dto.email = vendor.email
        dto.contactPerson = vendor.contactPerson
        dto.contactNumber = vendor.contactNumber
        dto.category = vendor.category
        dto.remarks = vendor.remarks
        dto.status = vendor.status
        dto.createdAt = vendor.createdAt
        dto.updatedAt = vendor.updatedAt
        
        // Load onboarding form if exists
        if (vendor.onboardingForm) {
            // Convert form to DTO (simplified - would need full mapping)
            dto.onboardingForm = new OnboardingFormDto()
            // Map form details...
        }
        
        // Load files
        dto.files = vendor.files.collect { toFileDto(it) }
        
        // Load follow-ups
        dto.followUps = followUpRepository.findByVendorIdOrderByCreatedAtDesc(vendorId)
            .collect { toFollowUpDto(it) }
        
        dto
    }
    
    @Override
    List<ActivityLogDto> getVendorActivityLog(Long vendorId) {
        activityLogRepository.findByVendorIdOrderByTimestampDesc(vendorId)
            .collect { toActivityLogDto(it) }
    }
    
    @Override
    VendorMetricsDto getMetrics() {
        VendorMetricsDto metrics = new VendorMetricsDto()
        
        Map<String, Long> statusCounts = [:]
        VendorStatus.values().each { status ->
            statusCounts[status.name()] = vendorRepository.countByStatus(status) ?: 0L
        }
        metrics.statusCounts = statusCounts
        
        metrics.totalVendors = vendorRepository.count()
        metrics.pendingFollowUps = followUpRepository.countAllPending() ?: 0L
        
        metrics
    }
    
    private VendorResponseDto toResponseDto(Vendor vendor) {
        VendorResponseDto dto = new VendorResponseDto()
        dto.id = vendor.id
        dto.name = vendor.name
        dto.email = vendor.email
        dto.contactPerson = vendor.contactPerson
        dto.contactNumber = vendor.contactNumber
        dto.category = vendor.category
        dto.remarks = vendor.remarks
        dto.status = vendor.status
        dto.createdAt = vendor.createdAt
        dto.updatedAt = vendor.updatedAt
        dto
    }
    
    private VendorFileDto toFileDto(VendorFile file) {
        VendorFileDto dto = new VendorFileDto()
        dto.id = file.id
        dto.vendorId = file.vendor.id
        dto.fileType = file.fileType
        dto.fileName = file.fileName
        dto.fileSize = file.fileSize
        dto.contentType = file.contentType
        dto.validationStatus = file.validationStatus
        dto.validationMessage = file.validationMessage
        dto.uploadedAt = file.uploadedAt
        dto
    }
    
    private FollowUpDto toFollowUpDto(FollowUp followUp) {
        FollowUpDto dto = new FollowUpDto()
        dto.id = followUp.id
        dto.vendorId = followUp.vendor.id
        dto.type = followUp.type
        dto.issueSummary = followUp.issueSummary
        dto.fieldsImpacted = followUp.fieldsImpacted
        dto.status = followUp.status
        dto.triggeredBy = followUp.triggeredBy
        dto.createdAt = followUp.createdAt
        dto.sentAt = followUp.sentAt
        dto.resolvedAt = followUp.resolvedAt
        dto
    }
    
    private ActivityLogDto toActivityLogDto(ActivityLog log) {
        ActivityLogDto dto = new ActivityLogDto()
        dto.id = log.id
        dto.vendorId = log.vendor.id
        dto.timestamp = log.timestamp
        dto.eventType = log.eventType
        dto.description = log.description
        dto.metadataJson = log.metadataJson
        dto
    }
}

