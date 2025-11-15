package com.vos.domain

import com.vos.enums.FileType
import com.vos.enums.ValidationStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "vendor_files")
class VendorFile extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    Vendor vendor
    
    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false)
    FileType fileType
    
    @Column(name = "file_name", nullable = false)
    String fileName
    
    @Column(name = "storage_key", nullable = false)
    String storageKey
    
    @Column(name = "file_size")
    Long fileSize
    
    @Column(name = "content_type")
    String contentType
    
    @Enumerated(EnumType.STRING)
    @Column(name = "validation_status", nullable = false)
    ValidationStatus validationStatus = ValidationStatus.PENDING
    
    @Column(name = "validation_message", length = 2000)
    String validationMessage
    
    @Column(name = "uploaded_at", nullable = false)
    LocalDateTime uploadedAt
    
    @PrePersist
    protected void onUpload() {
        if (uploadedAt == null) {
            uploadedAt = LocalDateTime.now()
        }
    }
}

