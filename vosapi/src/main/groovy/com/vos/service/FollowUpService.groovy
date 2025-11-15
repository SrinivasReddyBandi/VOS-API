package com.vos.service

import com.vos.model.dto.*

interface FollowUpService {
    FollowUpDto createFollowUp(Long vendorId, FollowUpRequestDto requestDto)
    List<FollowUpDto> getVendorFollowUps(Long vendorId)
    FollowUpDto updateFollowUp(Long followUpId, FollowUpUpdateDto updateDto)
    void triggerAutomaticFollowUps()
}

