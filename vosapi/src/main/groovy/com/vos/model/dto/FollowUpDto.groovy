package com.vos.model.dto

import com.vos.enums.FollowUpType
import com.vos.enums.TriggeredBy
import java.time.LocalDateTime

class FollowUpDto {
    Long id
    Long vendorId
    FollowUpType type
    String issueSummary
    String fieldsImpacted
    String status
    TriggeredBy triggeredBy
    LocalDateTime createdAt
    LocalDateTime sentAt
    LocalDateTime resolvedAt
}

