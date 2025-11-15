package com.vos.domain

import com.vos.enums.VendorStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "vendors")
class Vendor extends BaseEntity {
    
    @Column(name = "name", nullable = false)
    String name
    
    @Column(name = "email", nullable = false, unique = true)
    String email
    
    @Column(name = "contact_person")
    String contactPerson
    
    @Column(name = "contact_number")
    String contactNumber
    
    @Column(name = "category")
    String category
    
    @Column(name = "remarks", length = 2000)
    String remarks
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    VendorStatus status = VendorStatus.INVITED
    
    @Column(name = "invite_token", unique = true)
    String inviteToken
    
    @Column(name = "invite_token_expiry")
    LocalDateTime inviteTokenExpiry
    
    @Column(name = "otp")
    String otp
    
    @Column(name = "otp_expiry")
    LocalDateTime otpExpiry
    
    @Column(name = "otp_verified")
    Boolean otpVerified = false
    
    @OneToOne(mappedBy = "vendor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    VendorOnboardingForm onboardingForm
    
    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<VendorFile> files = []
    
    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<FollowUp> followUps = []
    
    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<ActivityLog> activityLogs = []
}

