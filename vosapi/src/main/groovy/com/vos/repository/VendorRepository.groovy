package com.vos.repository

import com.vos.domain.Vendor
import com.vos.enums.VendorStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

import java.time.LocalDateTime

@Repository
interface VendorRepository extends JpaRepository<Vendor, Long> {
    
    Optional<Vendor> findByEmail(String email)
    
    Optional<Vendor> findByInviteToken(String inviteToken)
    
    @Query("""SELECT v FROM Vendor v WHERE 
           (:status IS NULL OR v.status = :status) AND 
           (:name IS NULL OR LOWER(v.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND 
           (:category IS NULL OR v.category = :category) AND 
           (:email IS NULL OR LOWER(v.email) LIKE LOWER(CONCAT('%', :email, '%')))""")
    Page<Vendor> findWithFilters(
        @Param("status") VendorStatus status,
        @Param("name") String name,
        @Param("category") String category,
        @Param("email") String email,
        Pageable pageable
    )
    
    @Query("SELECT COUNT(v) FROM Vendor v WHERE v.status = :status")
    Long countByStatus(@Param("status") VendorStatus status)
    
    @Query("SELECT v FROM Vendor v WHERE v.inviteTokenExpiry < :now AND v.status = :status")
    List<Vendor> findExpiredInvitations(@Param("now") LocalDateTime now, @Param("status") VendorStatus status)
}

