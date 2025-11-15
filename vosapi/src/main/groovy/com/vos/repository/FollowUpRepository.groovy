package com.vos.repository

import com.vos.domain.FollowUp
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface FollowUpRepository extends JpaRepository<FollowUp, Long> {
    
    List<FollowUp> findByVendorIdOrderByCreatedAtDesc(Long vendorId)
    
    @Query("SELECT COUNT(f) FROM FollowUp f WHERE f.vendor.id = :vendorId AND f.status = 'PENDING'")
    Long countPendingByVendorId(@Param("vendorId") Long vendorId)
    
    @Query("SELECT COUNT(f) FROM FollowUp f WHERE f.status = 'PENDING'")
    Long countAllPending()
}

