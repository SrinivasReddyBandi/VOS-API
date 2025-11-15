package com.vos.repository

import com.vos.domain.ActivityLog
import com.vos.enums.ActivityEventType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    
    List<ActivityLog> findByVendorIdOrderByTimestampDesc(Long vendorId)
    
    List<ActivityLog> findByVendorIdAndEventTypeOrderByTimestampDesc(Long vendorId, ActivityEventType eventType)
}

