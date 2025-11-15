package com.vos.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class ContactDetails {
    
    @Column(name = "primary_contact_name", nullable = false)
    String primaryContactName
    
    @Column(name = "job_title", nullable = false)
    String jobTitle
    
    @Column(name = "email_address", nullable = false)
    String emailAddress
    
    @Column(name = "phone_number", nullable = false)
    String phoneNumber
    
    @Column(name = "secondary_contact_name")
    String secondaryContactName
    
    @Column(name = "secondary_contact_email")
    String secondaryContactEmail
    
    @Column(name = "website")
    String website
}

