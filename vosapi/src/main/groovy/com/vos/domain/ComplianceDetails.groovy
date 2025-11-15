package com.vos.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.time.LocalDate

@Embeddable
class ComplianceDetails {
    
    @Column(name = "tax_identification_number", nullable = false)
    String taxIdentificationNumber
    
    @Column(name = "business_license_number", nullable = false)
    String businessLicenseNumber
    
    @Column(name = "license_expiry_date", nullable = false)
    LocalDate licenseExpiryDate
    
    @Column(name = "insurance_provider", nullable = false)
    String insuranceProvider
    
    @Column(name = "insurance_policy_number", nullable = false)
    String insurancePolicyNumber
    
    @Column(name = "insurance_expiry_date", nullable = false)
    LocalDate insuranceExpiryDate
    
    @Column(name = "industry_certifications", length = 2000)
    String industryCertifications
}

