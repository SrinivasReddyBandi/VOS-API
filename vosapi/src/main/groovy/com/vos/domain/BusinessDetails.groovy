package com.vos.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class BusinessDetails {
    
    @Column(name = "legal_business_name", nullable = false)
    String legalBusinessName
    
    @Column(name = "business_registration_number", nullable = false)
    String businessRegistrationNumber
    
    @Column(name = "business_type", nullable = false)
    String businessType
    
    @Column(name = "year_established", nullable = false)
    Integer yearEstablished
    
    @Column(name = "business_address", nullable = false, length = 1000)
    String businessAddress
    
    @Column(name = "number_of_employees")
    Integer numberOfEmployees
    
    @Column(name = "industry_sector", nullable = false)
    String industrySector
}

