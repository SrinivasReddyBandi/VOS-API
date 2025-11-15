package com.vos.activity

import com.vos.domain.ActivityLog
import com.vos.domain.Vendor
import com.vos.enums.ActivityEventType
import com.vos.repository.ActivityLogRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import java.time.LocalDateTime

@Component
class ActivityLogger {
    
    private static final Logger logger = LoggerFactory.getLogger(ActivityLogger)
    private final ActivityLogRepository activityLogRepository
    
    ActivityLogger(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository
    }
    
    void log(Vendor vendor, ActivityEventType eventType, String description) {
        log(vendor, eventType, description, null)
    }
    
    void log(Vendor vendor, ActivityEventType eventType, String description, String metadataJson) {
        try {
            ActivityLog activityLog = new ActivityLog()
            activityLog.vendor = vendor
            activityLog.eventType = eventType
            activityLog.description = description
            activityLog.metadataJson = metadataJson
            activityLog.timestamp = LocalDateTime.now()
            
            activityLogRepository.save(activityLog)
            logger.debug("Activity logged: ${eventType} for vendor ${vendor.id}")
        } catch (Exception e) {
            logger.error("Error logging activity: ${e.message}", e)
        }
    }
}

