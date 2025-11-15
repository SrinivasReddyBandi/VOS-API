package com.vos.domain

import jakarta.persistence.*

@Entity
@Table(name = "vendor_onboarding_forms")
class VendorOnboardingForm extends BaseEntity {
    
    @OneToOne
    @JoinColumn(name = "vendor_id", nullable = false, unique = true)
    Vendor vendor
    
    @Embedded
    BusinessDetails businessDetails
    
    @Embedded
    ContactDetails contactDetails
    
    @Embedded
    BankingDetails bankingDetails
    
    @Embedded
    ComplianceDetails complianceDetails
    
    @Column(name = "business_doc_file_id")
    Long businessDocFileId
    
    @Column(name = "contact_doc_file_id")
    Long contactDocFileId
    
    @Column(name = "banking_doc_file_id")
    Long bankingDocFileId
    
    @Column(name = "compliance_doc_file_id")
    Long complianceDocFileId
}

