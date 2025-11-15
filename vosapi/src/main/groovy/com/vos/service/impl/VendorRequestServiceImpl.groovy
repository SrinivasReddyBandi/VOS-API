package com.vos.service.impl

import com.vos.activity.ActivityLogger
import com.vos.domain.Vendor
import com.vos.enums.ActivityEventType
import com.vos.enums.VendorStatus
import com.vos.exception.ResourceNotFoundException
import com.vos.exception.ValidationException
import com.vos.model.dto.VendorRequestDto
import com.vos.model.dto.VendorResponseDto
import com.vos.repository.VendorRepository
import com.vos.service.NotificationService
import com.vos.service.VendorRequestService
import com.vos.util.StringSanitizer
import com.vos.util.TokenGenerator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.time.LocalDateTime

@Service
@Transactional
class VendorRequestServiceImpl implements VendorRequestService {
    
    private static final Logger logger = LoggerFactory.getLogger(VendorRequestServiceImpl)
    private final VendorRepository vendorRepository
    private final NotificationService notificationService
    private final TokenGenerator tokenGenerator
    private final StringSanitizer stringSanitizer
    private final ActivityLogger activityLogger
    
    @Value('${app.invite.token.expiry.hours:168}')
    private int tokenExpiryHours
    
    @Value('${app.base.url:http://localhost:8080}')
    private String baseUrl
    
    VendorRequestServiceImpl(VendorRepository vendorRepository,
                            NotificationService notificationService,
                            TokenGenerator tokenGenerator,
                            StringSanitizer stringSanitizer,
                            ActivityLogger activityLogger) {
        this.vendorRepository = vendorRepository
        this.notificationService = notificationService
        this.tokenGenerator = tokenGenerator
        this.stringSanitizer = stringSanitizer
        this.activityLogger = activityLogger
    }

    @Override
    VendorResponseDto createVendorRequest(VendorRequestDto requestDto) {

        String email = stringSanitizer.sanitize(requestDto.vendorEmail)

        Vendor vendor = vendorRepository.findByEmail(email).orElse(null)

        // NEW VENDOR CREATION --------------------------------------
        if (vendor == null) {
            vendor = new Vendor()
            vendor.name = stringSanitizer.sanitize(requestDto.vendorName)
            vendor.email = email
            vendor.contactPerson = stringSanitizer.sanitize(requestDto.contactPerson)
            vendor.contactNumber = stringSanitizer.sanitize(requestDto.contactNumber)
            vendor.category = stringSanitizer.sanitize(requestDto.vendorCategory)
            vendor.remarks = stringSanitizer.sanitize(requestDto.remarks)
            vendor.status = VendorStatus.INVITED
        } else {
            // EXISTING VENDOR → UPDATE INFO -------------------------
            vendor.name = stringSanitizer.sanitize(requestDto.vendorName)
            vendor.contactPerson = stringSanitizer.sanitize(requestDto.contactPerson)
            vendor.contactNumber = stringSanitizer.sanitize(requestDto.contactNumber)
            vendor.category = stringSanitizer.sanitize(requestDto.vendorCategory)
            vendor.remarks = stringSanitizer.sanitize(requestDto.remarks)
            vendor.status = VendorStatus.INVITED
        }

        // ALWAYS (create + update): regenerate new token
        String inviteToken = tokenGenerator.generateSecureToken()
        vendor.inviteToken = inviteToken
        vendor.inviteTokenExpiry = LocalDateTime.now().plusHours(tokenExpiryHours)

        vendor = vendorRepository.save(vendor)

        // Log activity
        activityLogger.log(
                vendor,
                ActivityEventType.INVITATION_SENT,
                "Vendor invitation generated for ${vendor.email}"
        )

        // Build onboarding URL
        String inviteLink = "${baseUrl}/api/v1/vendor-sessions/${inviteToken}"

        // CLEAN PLAINTEXT EMAIL BODY (YOUR EXPECTED OUTPUT)
        String emailBody = """
Dear ${vendor.name},

You have been invited to complete the vendor onboarding process.
Please click the link below to access the onboarding form:

${inviteLink}

This link will expire in ${tokenExpiryHours} hours.

Best regards,
Vendor Onboarding System
""".stripIndent()

        try {
            notificationService.sendEmail(
                    vendor.email,
                    "Vendor Onboarding Invitation",
                    emailBody
            )
        } catch (Exception e) {
            logger.error("Failed to send invitation email: ${e.message}", e)
            // keep flow alive
        }

        return toResponseDto(vendor)
    }


    @Override
    VendorResponseDto getVendorRequest(Long id) {
        Vendor vendor = vendorRepository.findById(id)
            .orElseThrow { new ResourceNotFoundException("Vendor", id) }
        toResponseDto(vendor)
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
}

