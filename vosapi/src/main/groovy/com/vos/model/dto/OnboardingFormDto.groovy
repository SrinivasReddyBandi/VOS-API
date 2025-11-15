package com.vos.model.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.*
import java.time.LocalDate

class OnboardingFormDto {
    
    @Valid
    @NotNull(message = "Business details are required")
    BusinessDetailsDto businessDetails
    
    @Valid
    @NotNull(message = "Contact details are required")
    ContactDetailsDto contactDetails
    
    @Valid
    @NotNull(message = "Banking details are required")
    BankingDetailsDto bankingDetails
    
    @Valid
    @NotNull(message = "Compliance details are required")
    ComplianceDetailsDto complianceDetails
    
    Long businessDocFileId
    Long contactDocFileId
    Long bankingDocFileId
    Long complianceDocFileId
    
    static class BusinessDetailsDto {
        @NotBlank(message = "Legal business name is required")
        String legalBusinessName
        
        @NotBlank(message = "Business registration number is required")
        String businessRegistrationNumber
        
        @NotBlank(message = "Business type is required")
        String businessType
        
        @NotNull(message = "Year established is required")
        @Min(value = 1800, message = "Year must be valid")
        @Max(value = 2100, message = "Year must be valid")
        Integer yearEstablished
        
        @NotBlank(message = "Business address is required")
        String businessAddress
        
        Integer numberOfEmployees
        
        @NotBlank(message = "Industry sector is required")
        String industrySector
    }
    
    static class ContactDetailsDto {
        @NotBlank(message = "Primary contact name is required")
        String primaryContactName
        
        @NotBlank(message = "Job title is required")
        String jobTitle
        
        @NotBlank(message = "Email address is required")
        @Email(message = "Invalid email format")
        String emailAddress
        
        @NotBlank(message = "Phone number is required")
        String phoneNumber
        
        String secondaryContactName
        
        @Email(message = "Invalid email format")
        String secondaryContactEmail
        
        String website
    }
    
    static class BankingDetailsDto {
        @NotBlank(message = "Bank name is required")
        String bankName
        
        @NotBlank(message = "Account holder name is required")
        String accountHolderName
        
        @NotBlank(message = "Account number is required")
        @Pattern(regexp = "\\d{8,18}", message = "Account number must be 8-18 digits")
        String accountNumber
        
        @NotBlank(message = "Account type is required")
        String accountType
        
        @NotBlank(message = "Routing or SWIFT code is required")
        String routingOrSwiftCode
        
        String iban
        
        @NotBlank(message = "Payment terms are required")
        String paymentTerms
        
        @NotBlank(message = "Currency is required")
        String currency
    }
    
    static class ComplianceDetailsDto {
        @NotBlank(message = "Tax identification number is required")
        String taxIdentificationNumber
        
        @NotBlank(message = "Business license number is required")
        String businessLicenseNumber
        
        @NotNull(message = "License expiry date is required")
        @Future(message = "License expiry date must be in the future")
        LocalDate licenseExpiryDate
        
        @NotBlank(message = "Insurance provider is required")
        String insuranceProvider
        
        @NotBlank(message = "Insurance policy number is required")
        String insurancePolicyNumber
        
        @NotNull(message = "Insurance expiry date is required")
        @Future(message = "Insurance expiry date must be in the future")
        LocalDate insuranceExpiryDate
        
        String industryCertifications
    }
}

