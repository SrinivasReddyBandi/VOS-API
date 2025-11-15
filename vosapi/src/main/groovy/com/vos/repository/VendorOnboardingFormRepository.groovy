package com.vos.repository

import com.vos.domain.VendorOnboardingForm
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VendorOnboardingFormRepository extends JpaRepository<VendorOnboardingForm, Long> {
    
    Optional<VendorOnboardingForm> findByVendorId(Long vendorId)
}

