package com.vos.service.impl

import com.vos.activity.ActivityLogger
import com.vos.domain.FollowUp
import com.vos.domain.Vendor
import com.vos.enums.ActivityEventType
import com.vos.enums.FollowUpType
import com.vos.enums.TriggeredBy
import com.vos.exception.ResourceNotFoundException
import com.vos.model.dto.*
import com.vos.repository.FollowUpRepository
import com.vos.repository.VendorRepository
import com.vos.service.FollowUpService
import com.vos.service.NotificationService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.time.LocalDateTime

@Service
@Transactional
class FollowUpServiceImpl implements FollowUpService {
    
    private static final Logger logger = LoggerFactory.getLogger(FollowUpServiceImpl)
    private final FollowUpRepository followUpRepository
    private final VendorRepository vendorRepository
    private final NotificationService notificationService
    private final ActivityLogger activityLogger
    
    FollowUpServiceImpl(FollowUpRepository followUpRepository,
                       VendorRepository vendorRepository,
                       NotificationService notificationService,
                       ActivityLogger activityLogger) {
        this.followUpRepository = followUpRepository
        this.vendorRepository = vendorRepository
        this.notificationService = notificationService
        this.activityLogger = activityLogger
    }
    
    @Override
    FollowUpDto createFollowUp(Long vendorId, FollowUpRequestDto requestDto) {
        Vendor vendor = vendorRepository.findById(vendorId)
            .orElseThrow { new ResourceNotFoundException("Vendor", vendorId) }
        
        FollowUp followUp = new FollowUp()
        followUp.vendor = vendor
        followUp.type = requestDto.type
        followUp.issueSummary = requestDto.issueSummary
        followUp.fieldsImpacted = requestDto.fieldsImpacted
        followUp.status = "PENDING"
        followUp.triggeredBy = TriggeredBy.PROCUREMENT_USER
        
        // Generate email body
        String emailBody = """
            Dear ${vendor.name},
            
            We need your attention regarding your vendor onboarding application.
            
            Issue: ${requestDto.issueSummary}
            ${requestDto.fieldsImpacted ? "Fields impacted: ${requestDto.fieldsImpacted}" : ""}
            
            Please review and update your information as soon as possible.
            
            Best regards,
            Vendor Onboarding System
        """.stripIndent()
        
        followUp.emailBody = emailBody
        
        followUp = followUpRepository.save(followUp)
        
        // Send email
        try {
            notificationService.sendEmail(vendor.email, 
                "Action Required: Vendor Onboarding", emailBody)
            followUp.sentAt = LocalDateTime.now()
            followUp = followUpRepository.save(followUp)
        } catch (Exception e) {
            logger.error("Failed to send follow-up email: ${e.message}", e)
        }
        
        // Log activity
        activityLogger.log(vendor, ActivityEventType.FOLLOW_UP_TRIGGERED, 
            "Follow-up created: ${requestDto.issueSummary}")
        
        toFollowUpDto(followUp)
    }
    
    @Override
    List<FollowUpDto> getVendorFollowUps(Long vendorId) {
        followUpRepository.findByVendorIdOrderByCreatedAtDesc(vendorId)
            .collect { toFollowUpDto(it) }
    }
    
    @Override
    FollowUpDto updateFollowUp(Long followUpId, FollowUpUpdateDto updateDto) {
        FollowUp followUp = followUpRepository.findById(followUpId)
            .orElseThrow { new ResourceNotFoundException("FollowUp", followUpId) }
        
        if (updateDto.status) {
            followUp.status = updateDto.status
            if (updateDto.status == "RESOLVED" && followUp.resolvedAt == null) {
                followUp.resolvedAt = LocalDateTime.now()
            }
        }
        
        followUp = followUpRepository.save(followUp)
        
        // Log activity
        activityLogger.log(followUp.vendor, ActivityEventType.FOLLOW_UP_RESOLVED, 
            "Follow-up ${followUpId} status updated to ${updateDto.status}")
        
        toFollowUpDto(followUp)
    }
    
    @Override
    void triggerAutomaticFollowUps() {
        // This would be called by a scheduled task
        // Logic to automatically create follow-ups based on:
        // - Missing data
        // - Invalid files
        // - Delayed responses
        // Implementation would check vendors and create follow-ups as needed
        logger.info("Automatic follow-ups triggered")
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
}

