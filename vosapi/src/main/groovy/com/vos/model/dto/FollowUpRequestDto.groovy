package com.vos.model.dto

import com.vos.enums.FollowUpType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

class FollowUpRequestDto {
    
    @NotNull(message = "Follow-up type is required")
    FollowUpType type
    
    @NotBlank(message = "Issue summary is required")
    String issueSummary
    
    String fieldsImpacted
}

