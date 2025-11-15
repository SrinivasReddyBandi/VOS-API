package com.vos.model.dto

import com.vos.enums.FileType
import com.vos.enums.ValidationStatus
import java.time.LocalDateTime

class VendorFileDto {
    Long id
    Long vendorId
    FileType fileType
    String fileName
    Long fileSize
    String contentType
    ValidationStatus validationStatus
    String validationMessage
    LocalDateTime uploadedAt
}

