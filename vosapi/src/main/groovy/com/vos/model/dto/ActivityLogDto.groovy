package com.vos.model.dto

import com.vos.enums.ActivityEventType
import java.time.LocalDateTime

class ActivityLogDto {
    Long id
    Long vendorId
    LocalDateTime timestamp
    ActivityEventType eventType
    String description
    String metadataJson
}

