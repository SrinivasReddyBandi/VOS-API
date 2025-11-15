package com.vos.domain

import com.vos.enums.FollowUpType
import com.vos.enums.TriggeredBy
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "follow_ups")
class FollowUp extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    Vendor vendor
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    FollowUpType type
    
    @Column(name = "issue_summary", nullable = false, length = 2000)
    String issueSummary
    
    @Column(name = "fields_impacted", length = 1000)
    String fieldsImpacted
    
    @Column(name = "status", nullable = false)
    String status = "PENDING"
    
    @Enumerated(EnumType.STRING)
    @Column(name = "triggered_by", nullable = false)
    TriggeredBy triggeredBy
    
    @Column(name = "email_body", length = 5000)
    String emailBody
    
    @Column(name = "sent_at")
    LocalDateTime sentAt
    
    @Column(name = "resolved_at")
    LocalDateTime resolvedAt
}

