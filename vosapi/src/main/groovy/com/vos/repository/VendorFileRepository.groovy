package com.vos.repository

import com.vos.domain.VendorFile
import com.vos.enums.FileType
import com.vos.enums.ValidationStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VendorFileRepository extends JpaRepository<VendorFile, Long> {
    
    List<VendorFile> findByVendorId(Long vendorId)
    
    Optional<VendorFile> findByVendorIdAndFileType(Long vendorId, FileType fileType)
    
    List<VendorFile> findByVendorIdAndValidationStatus(Long vendorId, ValidationStatus validationStatus)
}

