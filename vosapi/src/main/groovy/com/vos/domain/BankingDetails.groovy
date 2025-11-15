package com.vos.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class BankingDetails {
    
    @Column(name = "bank_name", nullable = false)
    String bankName
    
    @Column(name = "account_holder_name", nullable = false)
    String accountHolderName
    
    @Column(name = "account_number", nullable = false)
    String accountNumber
    
    @Column(name = "account_type", nullable = false)
    String accountType
    
    @Column(name = "routing_or_swift_code", nullable = false)
    String routingOrSwiftCode
    
    @Column(name = "iban")
    String iban
    
    @Column(name = "payment_terms", nullable = false)
    String paymentTerms
    
    @Column(name = "currency", nullable = false)
    String currency
}

