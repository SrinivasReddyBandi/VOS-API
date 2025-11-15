package com.vos.domain

import com.vos.enums.ActivityEventType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "activity_logs")
class ActivityLog extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    Vendor vendor
    
    @Column(name = "timestamp", nullable = false)
    LocalDateTime timestamp
    
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    ActivityEventType eventType
    
    @Column(name = "description", nullable = false, length = 2000)
    String description
    
    @Column(name = "metadata_json", columnDefinition = "TEXT")
    String metadataJson
    
    @PrePersist
    protected void onLog() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now()
        }
    }
}

