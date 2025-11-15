package com.vos.service.impl

import com.vos.domain.*
import com.vos.enums.VendorStatus
import com.vos.exception.ResourceNotFoundException
import com.vos.model.dto.*
import com.vos.repository.*
import com.vos.service.VendorDashboardService
import com.vos.util.PdfValidator
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Service
@Transactional(readOnly = true)
class VendorDashboardServiceImpl implements VendorDashboardService {
    
    private final VendorRepository vendorRepository
    private final FollowUpRepository followUpRepository
    private final ActivityLogRepository activityLogRepository
    private final VendorFileRepository fileRepository
    private final PdfValidator pdfValidator

    // file storage location (must match onboarding service)
    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize()

    VendorDashboardServiceImpl(VendorRepository vendorRepository,
                              FollowUpRepository followUpRepository,
                              ActivityLogRepository activityLogRepository,
                              VendorFileRepository fileRepository,
                              PdfValidator pdfValidator) {
        this.vendorRepository = vendorRepository
        this.followUpRepository = followUpRepository
        this.activityLogRepository = activityLogRepository
        this.fileRepository = fileRepository
        this.pdfValidator = pdfValidator
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
            // Convert form to DTO (flattened mapping)
            OnboardingFormDto formDto = new OnboardingFormDto()
            formDto.legalBusinessName = vendor.onboardingForm.businessDetails?.legalBusinessName
            formDto.businessRegistrationNumber = vendor.onboardingForm.businessDetails?.businessRegistrationNumber
            formDto.businessType = vendor.onboardingForm.businessDetails?.businessType
            formDto.yearEstablished = vendor.onboardingForm.businessDetails?.yearEstablished
            formDto.businessAddress = vendor.onboardingForm.businessDetails?.businessAddress
            formDto.numberOfEmployees = vendor.onboardingForm.businessDetails?.numberOfEmployees
            formDto.industrySector = vendor.onboardingForm.businessDetails?.industrySector

            formDto.primaryContactName = vendor.onboardingForm.contactDetails?.primaryContactName
            formDto.jobTitle = vendor.onboardingForm.contactDetails?.jobTitle
            formDto.emailAddress = vendor.onboardingForm.contactDetails?.emailAddress
            formDto.phoneNumber = vendor.onboardingForm.contactDetails?.phoneNumber
            formDto.secondaryContactName = vendor.onboardingForm.contactDetails?.secondaryContactName
            formDto.secondaryContactEmail = vendor.onboardingForm.contactDetails?.secondaryContactEmail
            formDto.website = vendor.onboardingForm.contactDetails?.website

            formDto.bankName = vendor.onboardingForm.bankingDetails?.bankName
            formDto.accountHolderName = vendor.onboardingForm.bankingDetails?.accountHolderName
            formDto.accountNumber = vendor.onboardingForm.bankingDetails?.accountNumber
            formDto.accountType = vendor.onboardingForm.bankingDetails?.accountType
            formDto.routingOrSwiftCode = vendor.onboardingForm.bankingDetails?.routingOrSwiftCode
            formDto.iban = vendor.onboardingForm.bankingDetails?.iban
            formDto.paymentTerms = vendor.onboardingForm.bankingDetails?.paymentTerms
            formDto.currency = vendor.onboardingForm.bankingDetails?.currency

            formDto.taxIdentificationNumber = vendor.onboardingForm.complianceDetails?.taxIdentificationNumber
            formDto.businessLicenseNumber = vendor.onboardingForm.complianceDetails?.businessLicenseNumber
            formDto.licenseExpiryDate = vendor.onboardingForm.complianceDetails?.licenseExpiryDate
            formDto.insuranceProvider = vendor.onboardingForm.complianceDetails?.insuranceProvider
            formDto.insurancePolicyNumber = vendor.onboardingForm.complianceDetails?.insurancePolicyNumber
            formDto.insuranceExpiryDate = vendor.onboardingForm.complianceDetails?.insuranceExpiryDate
            formDto.industryCertifications = vendor.onboardingForm.complianceDetails?.industryCertifications

            formDto.businessDocFileId = vendor.onboardingForm.businessDocFileId
            formDto.contactDocFileId = vendor.onboardingForm.contactDocFileId
            formDto.bankingDocFileId = vendor.onboardingForm.bankingDocFileId
            formDto.complianceDocFileId = vendor.onboardingForm.complianceDocFileId

            dto.onboardingForm = formDto
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

    @Override
    List<VendorFileDto> getVendorFiles(Long vendorId) {
        vendorRepository.findById(vendorId).orElseThrow { new ResourceNotFoundException("Vendor", vendorId) }
        fileRepository.findByVendorId(vendorId).collect { toFileDto(it) }
    }

    @Override
    VendorFileDto getVendorFile(Long vendorId, Long fileId) {
        vendorRepository.findById(vendorId).orElseThrow { new ResourceNotFoundException("Vendor", vendorId) }
        VendorFile file = fileRepository.findById(fileId)
            .orElseThrow { new ResourceNotFoundException("VendorFile", fileId) }
        if (file.vendor.id != vendorId) {
            throw new ResourceNotFoundException("VendorFile", fileId)
        }
        toFileDto(file)
    }

    @Override
    Resource downloadFile(Long vendorId, Long fileId) {
        VendorFile file = fileRepository.findById(fileId)
            .orElseThrow { new ResourceNotFoundException("VendorFile", fileId) }
        if (file.vendor.id != vendorId) {
            throw new ResourceNotFoundException("VendorFile", fileId)
        }

        Path filePath = fileStorageLocation.resolve(file.storageKey).normalize()
        if (!Files.exists(filePath)) {
            throw new ResourceNotFoundException("File storage", file.storageKey)
        }
        new FileSystemResource(filePath.toFile())
    }

    @Override
    VendorFileDto revalidateFile(Long vendorId, Long fileId) {
        VendorFile file = fileRepository.findById(fileId)
            .orElseThrow { new ResourceNotFoundException("VendorFile", fileId) }
        if (file.vendor.id != vendorId) {
            throw new ResourceNotFoundException("VendorFile", fileId)
        }

        Path filePath = fileStorageLocation.resolve(file.storageKey).normalize()
        if (!Files.exists(filePath)) {
            throw new ResourceNotFoundException("File storage", file.storageKey)
        }

        try (InputStream is = Files.newInputStream(filePath)) {
            def result = pdfValidator.validatePdf(is, file.fileType, file.fileName)
            file.validationStatus = result.validationStatus
            file.validationMessage = result.validationMessage
            fileRepository.save(file)
            activityLogRepository.save(new ActivityLog(vendor: file.vendor, eventType: ActivityEventType.FILE_VALIDATED, description: "Re-validated file ${file.fileName} with status ${result.validationStatus}"))
        }

        toFileDto(file)
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
