package com.vos.model.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

class VendorRequestDto {
    
    @NotBlank(message = "Vendor name is required")
    String vendorName
    
    @NotBlank(message = "Vendor email is required")
    @Email(message = "Invalid email format")
    String vendorEmail
    
    String contactPerson
    String contactNumber
    String vendorCategory
    String remarks
}

