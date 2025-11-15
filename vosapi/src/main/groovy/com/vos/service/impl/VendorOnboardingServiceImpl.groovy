package com.vos.service.impl

import com.vos.activity.ActivityLogger
import com.vos.domain.*
import com.vos.enums.*
import com.vos.exception.ResourceNotFoundException
import com.vos.exception.ValidationException
import com.vos.model.dto.OnboardingFormDto
import com.vos.model.dto.VendorFileDto
import com.vos.repository.*
import com.vos.service.VendorOnboardingService
import com.vos.util.PdfValidator
import com.vos.util.StringSanitizer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Service
@Transactional
class VendorOnboardingServiceImpl implements VendorOnboardingService {
    
    private static final Logger logger = LoggerFactory.getLogger(VendorOnboardingServiceImpl)
    private final VendorRepository vendorRepository
    private final VendorOnboardingFormRepository formRepository
    private final VendorFileRepository fileRepository
    private final StringSanitizer stringSanitizer
    private final PdfValidator pdfValidator
    private final ActivityLogger activityLogger
    
    private final Path fileStorageLocation
    
    VendorOnboardingServiceImpl(VendorRepository vendorRepository,
                                VendorOnboardingFormRepository formRepository,
                                VendorFileRepository fileRepository,
                                StringSanitizer stringSanitizer,
                                PdfValidator pdfValidator,
                                ActivityLogger activityLogger) {
        this.vendorRepository = vendorRepository
        this.formRepository = formRepository
        this.fileRepository = fileRepository
        this.stringSanitizer = stringSanitizer
        this.pdfValidator = pdfValidator
        this.activityLogger = activityLogger
        
        // Initialize file storage
        this.fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize()
        try {
            Files.createDirectories(this.fileStorageLocation)
        } catch (Exception e) {
            logger.error("Could not create upload directory", e)
        }
    }
    
    @Override
    OnboardingFormDto submitOnboardingForm(Long vendorId, OnboardingFormDto formDto) {
        Vendor vendor = vendorRepository.findById(vendorId)
            .orElseThrow { new ResourceNotFoundException("Vendor", vendorId) }
        
        if (vendor.status != VendorStatus.OTP_VERIFIED) {
            throw new ValidationException("Vendor must verify OTP before submitting form")
        }
        
        // Validate business logic
        validateForm(formDto)
        
        // Get or create form
        VendorOnboardingForm form = formRepository.findByVendorId(vendorId)
            .orElse(new VendorOnboardingForm())
        
        form.vendor = vendor
        
        // Map business details (flattened DTO)
        BusinessDetails businessDetails = new BusinessDetails()
        businessDetails.legalBusinessName = stringSanitizer.sanitize(formDto.legalBusinessName)
        businessDetails.businessRegistrationNumber = stringSanitizer.sanitize(formDto.businessRegistrationNumber)
        businessDetails.businessType = stringSanitizer.sanitize(formDto.businessType)
        businessDetails.yearEstablished = formDto.yearEstablished
        businessDetails.businessAddress = stringSanitizer.sanitize(formDto.businessAddress)
        businessDetails.numberOfEmployees = formDto.numberOfEmployees
        businessDetails.industrySector = stringSanitizer.sanitize(formDto.industrySector)
        form.businessDetails = businessDetails

        // Map contact details (flattened DTO)
        ContactDetails contactDetails = new ContactDetails()
        contactDetails.primaryContactName = stringSanitizer.sanitize(formDto.primaryContactName)
        contactDetails.jobTitle = stringSanitizer.sanitize(formDto.jobTitle)
        contactDetails.emailAddress = stringSanitizer.sanitize(formDto.emailAddress)
        contactDetails.phoneNumber = stringSanitizer.sanitize(formDto.phoneNumber)
        contactDetails.secondaryContactName = stringSanitizer.sanitize(formDto.secondaryContactName)
        contactDetails.secondaryContactEmail = stringSanitizer.sanitize(formDto.secondaryContactEmail)
        contactDetails.website = stringSanitizer.sanitize(formDto.website)
        form.contactDetails = contactDetails

        // Map banking details (flattened DTO)
        BankingDetails bankingDetails = new BankingDetails()
        bankingDetails.bankName = stringSanitizer.sanitize(formDto.bankName)
        bankingDetails.accountHolderName = stringSanitizer.sanitize(formDto.accountHolderName)
        bankingDetails.accountNumber = stringSanitizer.sanitize(formDto.accountNumber)
        bankingDetails.accountType = stringSanitizer.sanitize(formDto.accountType)
        bankingDetails.routingOrSwiftCode = stringSanitizer.sanitize(formDto.routingOrSwiftCode)
        bankingDetails.iban = stringSanitizer.sanitize(formDto.iban)
        bankingDetails.paymentTerms = stringSanitizer.sanitize(formDto.paymentTerms)
        bankingDetails.currency = stringSanitizer.sanitize(formDto.currency)
        form.bankingDetails = bankingDetails

        // Map compliance details (flattened DTO)
        ComplianceDetails complianceDetails = new ComplianceDetails()
        complianceDetails.taxIdentificationNumber = stringSanitizer.sanitize(formDto.taxIdentificationNumber)
        complianceDetails.businessLicenseNumber = stringSanitizer.sanitize(formDto.businessLicenseNumber)
        complianceDetails.licenseExpiryDate = formDto.licenseExpiryDate
        complianceDetails.insuranceProvider = stringSanitizer.sanitize(formDto.insuranceProvider)
        complianceDetails.insurancePolicyNumber = stringSanitizer.sanitize(formDto.insurancePolicyNumber)
        complianceDetails.insuranceExpiryDate = formDto.insuranceExpiryDate
        complianceDetails.industryCertifications = stringSanitizer.sanitize(formDto.industryCertifications)
        form.complianceDetails = complianceDetails

        // Set file IDs
        form.businessDocFileId = formDto.businessDocFileId
        form.contactDocFileId = formDto.contactDocFileId
        form.bankingDocFileId = formDto.bankingDocFileId
        form.complianceDocFileId = formDto.complianceDocFileId
        
        form = formRepository.save(form)
        
        // Update vendor status
        vendor.status = VendorStatus.WAITING_FOR_VALIDATION
        vendorRepository.save(vendor)
        
        // Log activity
        activityLogger.log(vendor, ActivityEventType.FORM_SUBMITTED, 
            "Onboarding form submitted for vendor ${vendor.id}")
        
        // Convert back to DTO
        toFormDto(form)
    }
    
    @Override
    VendorFileDto uploadFile(Long vendorId, FileType fileType, MultipartFile file) {
        Vendor vendor = vendorRepository.findById(vendorId)
            .orElseThrow { new ResourceNotFoundException("Vendor", vendorId) }
        
        if (file.isEmpty()) {
            throw new ValidationException("File is empty")
        }
        
        if (!file.contentType.equals("application/pdf")) {
            throw new ValidationException("Only PDF files are allowed")
        }
        
        // Generate unique file name
        String originalFileName = file.originalFilename
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."))
        String storageKey = UUID.randomUUID().toString() + fileExtension
        
        // Save file
        Path targetLocation = fileStorageLocation.resolve(storageKey)
        try {
            Files.copy(file.inputStream, targetLocation)
        } catch (Exception e) {
            logger.error("Error saving file: ${e.message}", e)
            throw new RuntimeException("Failed to save file", e)
        }
        
        // Create file record
        VendorFile vendorFile = new VendorFile()
        vendorFile.vendor = vendor
        vendorFile.fileType = fileType
        vendorFile.fileName = originalFileName
        vendorFile.storageKey = storageKey
        vendorFile.fileSize = file.size
        vendorFile.contentType = file.contentType
        vendorFile.validationStatus = ValidationStatus.PENDING
        vendorFile.uploadedAt = LocalDateTime.now()
        
        vendorFile = fileRepository.save(vendorFile)
        
        // Validate PDF asynchronously (in real app, use @Async)
        try {
            PdfValidator.ValidationResult result = pdfValidator.validatePdf(
                Files.newInputStream(targetLocation), fileType, originalFileName)
            vendorFile.validationStatus = result.validationStatus
            vendorFile.validationMessage = result.validationMessage
            vendorFile = fileRepository.save(vendorFile)
            
            activityLogger.log(vendor, ActivityEventType.FILE_VALIDATED, 
                "File ${originalFileName} validated with status ${result.validationStatus}")
        } catch (Exception e) {
            logger.error("Error validating PDF: ${e.message}", e)
            vendorFile.validationStatus = ValidationStatus.INVALID
            vendorFile.validationMessage = "Error during validation: ${e.message}"
            vendorFile = fileRepository.save(vendorFile)
        }
        
        // Log activity
        activityLogger.log(vendor, ActivityEventType.FILE_UPLOADED, 
            "File ${originalFileName} uploaded for ${fileType}")
        
        // Convert to DTO
        toFileDto(vendorFile)
    }
    
    private void validateForm(OnboardingFormDto formDto) {
        // Validate dates are in future (flattened DTO)
        if (formDto.licenseExpiryDate != null && formDto.licenseExpiryDate.isBefore(LocalDate.now())) {
            throw new ValidationException("License expiry date must be in the future")
        }
        if (formDto.insuranceExpiryDate != null && formDto.insuranceExpiryDate.isBefore(LocalDate.now())) {
            throw new ValidationException("Insurance expiry date must be in the future")
        }
        
        // Validate account number format
        if (formDto.accountNumber && !formDto.accountNumber.matches("\\d{8,18}")) {
            throw new ValidationException("Account number must be 8-18 digits")
        }
    }
    
    private OnboardingFormDto toFormDto(VendorOnboardingForm form) {
        OnboardingFormDto dto = new OnboardingFormDto()
        
        // Flattened mapping: copy embedded fields directly to top-level DTO
        dto.id = form.id
        dto.legalBusinessName = form.businessDetails?.legalBusinessName
        dto.businessRegistrationNumber = form.businessDetails?.businessRegistrationNumber
        dto.businessType = form.businessDetails?.businessType
        dto.yearEstablished = form.businessDetails?.yearEstablished
        dto.businessAddress = form.businessDetails?.businessAddress
        dto.numberOfEmployees = form.businessDetails?.numberOfEmployees
        dto.industrySector = form.businessDetails?.industrySector

        dto.primaryContactName = form.contactDetails?.primaryContactName
        dto.jobTitle = form.contactDetails?.jobTitle
        dto.emailAddress = form.contactDetails?.emailAddress
        dto.phoneNumber = form.contactDetails?.phoneNumber
        dto.secondaryContactName = form.contactDetails?.secondaryContactName
        dto.secondaryContactEmail = form.contactDetails?.secondaryContactEmail
        dto.website = form.contactDetails?.website

        dto.bankName = form.bankingDetails?.bankName
        dto.accountHolderName = form.bankingDetails?.accountHolderName
        dto.accountNumber = form.bankingDetails?.accountNumber
        dto.accountType = form.bankingDetails?.accountType
        dto.routingOrSwiftCode = form.bankingDetails?.routingOrSwiftCode
        dto.iban = form.bankingDetails?.iban
        dto.paymentTerms = form.bankingDetails?.paymentTerms
        dto.currency = form.bankingDetails?.currency

        dto.taxIdentificationNumber = form.complianceDetails?.taxIdentificationNumber
        dto.businessLicenseNumber = form.complianceDetails?.businessLicenseNumber
        dto.licenseExpiryDate = form.complianceDetails?.licenseExpiryDate
        dto.insuranceProvider = form.complianceDetails?.insuranceProvider
        dto.insurancePolicyNumber = form.complianceDetails?.insurancePolicyNumber
        dto.insuranceExpiryDate = form.complianceDetails?.insuranceExpiryDate
        dto.industryCertifications = form.complianceDetails?.industryCertifications

        dto.businessDocFileId = form.businessDocFileId
        dto.contactDocFileId = form.contactDocFileId
        dto.bankingDocFileId = form.bankingDocFileId
        dto.complianceDocFileId = form.complianceDocFileId
        
        dto
    }
    
    private VendorFileDto toFileDto(VendorFile vendorFile) {
        VendorFileDto dto = new VendorFileDto()
        dto.id = vendorFile.id
        dto.vendorId = vendorFile.vendor.id
        dto.fileType = vendorFile.fileType
        dto.fileName = vendorFile.fileName
        dto.fileSize = vendorFile.fileSize
        dto.contentType = vendorFile.contentType
        dto.validationStatus = vendorFile.validationStatus
        dto.validationMessage = vendorFile.validationMessage
        dto.uploadedAt = vendorFile.uploadedAt
        dto
    }
}
