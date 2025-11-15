package com.vos.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@MappedSuperclass
abstract class BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id
    
    @Column(name = "created_at", nullable = false, updatable = false)
    LocalDateTime createdAt
    
    @Column(name = "updated_at")
    LocalDateTime updatedAt
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now()
        updatedAt = LocalDateTime.now()
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now()
    }
}

