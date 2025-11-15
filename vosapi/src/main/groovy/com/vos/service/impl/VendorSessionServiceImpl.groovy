package com.vos.service.impl

import com.vos.activity.ActivityLogger
import com.vos.domain.Vendor
import com.vos.enums.ActivityEventType
import com.vos.enums.VendorStatus
import com.vos.exception.InvalidOtpException
import com.vos.exception.InvalidTokenException
import com.vos.model.dto.OtpVerifyDto
import com.vos.model.dto.VendorSessionDto
import com.vos.repository.VendorRepository
import com.vos.service.NotificationService
import com.vos.service.VendorSessionService
import com.vos.util.TokenGenerator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.time.LocalDateTime

@Service
@Transactional
class VendorSessionServiceImpl implements VendorSessionService {
    
    private static final Logger logger = LoggerFactory.getLogger(VendorSessionServiceImpl)
    private final VendorRepository vendorRepository
    private final NotificationService notificationService
    private final TokenGenerator tokenGenerator
    private final ActivityLogger activityLogger
    
    @Value('${app.otp.expiry.minutes:10}')
    private int otpExpiryMinutes
    
    VendorSessionServiceImpl(VendorRepository vendorRepository,
                            NotificationService notificationService,
                            TokenGenerator tokenGenerator,
                            ActivityLogger activityLogger) {
        this.vendorRepository = vendorRepository
        this.notificationService = notificationService
        this.tokenGenerator = tokenGenerator
        this.activityLogger = activityLogger
    }
    
    @Override
    VendorSessionDto getSessionByToken(String token) {
        Vendor vendor = vendorRepository.findByInviteToken(token)
            .orElseThrow { new InvalidTokenException("Invalid or expired invitation token") }
        
        if (vendor.inviteTokenExpiry != null && vendor.inviteTokenExpiry.isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Invitation token has expired")
        }
        
        toSessionDto(vendor)
    }
    
    @Override
    void sendOtp(String token) {
        Vendor vendor = vendorRepository.findByInviteToken(token)
            .orElseThrow { new InvalidTokenException("Invalid or expired invitation token") }
        
        if (vendor.inviteTokenExpiry != null && vendor.inviteTokenExpiry.isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Invitation token has expired")
        }
        
        // Generate OTP
        String otp = tokenGenerator.generateOtp()
        vendor.otp = otp
        vendor.otpExpiry = LocalDateTime.now().plusMinutes(otpExpiryMinutes)
        vendor = vendorRepository.save(vendor)
        
        // Log activity
        activityLogger.log(vendor, ActivityEventType.OTP_SENT, 
            "OTP sent to ${vendor.email}")
        
        // Send OTP email
        String emailBody = """
            Dear ${vendor.name},
            
            Your OTP for vendor onboarding is: ${otp}
            
            This OTP will expire in ${otpExpiryMinutes} minutes.
            
            If you did not request this OTP, please ignore this email.
            
            Best regards,
            Vendor Onboarding System
        """.stripIndent()
        
        try {
            notificationService.sendEmail(vendor.email, "Vendor Onboarding OTP", emailBody)
        } catch (Exception e) {
            logger.error("Failed to send OTP email: ${e.message}", e)
            throw new RuntimeException("Failed to send OTP", e)
        }
    }
    
    @Override
    VendorSessionDto verifyOtp(String token, OtpVerifyDto otpVerifyDto) {
        Vendor vendor = vendorRepository.findByInviteToken(token)
            .orElseThrow { new InvalidTokenException("Invalid or expired invitation token") }
        
        if (vendor.otp == null || !vendor.otp.equals(otpVerifyDto.otp)) {
            throw new InvalidOtpException("Invalid OTP")
        }
        
        if (vendor.otpExpiry == null || vendor.otpExpiry.isBefore(LocalDateTime.now())) {
            throw new InvalidOtpException("OTP has expired")
        }
        
        // Verify OTP
        vendor.otpVerified = true
        vendor.status = VendorStatus.OTP_VERIFIED
        vendor.otp = null // Clear OTP after verification
        vendor = vendorRepository.save(vendor)
        
        // Log activity
        activityLogger.log(vendor, ActivityEventType.OTP_VERIFIED, 
            "OTP verified for ${vendor.email}")
        
        toSessionDto(vendor)
    }
    
    private VendorSessionDto toSessionDto(Vendor vendor) {
        VendorSessionDto dto = new VendorSessionDto()
        dto.vendorId = vendor.id
        dto.vendorName = vendor.name
        dto.vendorEmail = vendor.email
        dto.status = vendor.status
        dto.otpVerified = vendor.otpVerified
        dto.tokenExpiry = vendor.inviteTokenExpiry
        dto
    }
}

