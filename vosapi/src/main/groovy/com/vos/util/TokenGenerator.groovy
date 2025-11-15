package com.vos.util

import org.springframework.stereotype.Component

import java.security.SecureRandom
import java.util.Base64

@Component
class TokenGenerator {
    
    private static final SecureRandom secureRandom = new SecureRandom()
    private static final Base64.Encoder base64Encoder = Base64.urlEncoder
    
    String generateSecureToken() {
        byte[] randomBytes = new byte[32]
        secureRandom.nextBytes(randomBytes)
        base64Encoder.encodeToString(randomBytes)
    }
    
    String generateOtp() {
        int otp = 100000 + secureRandom.nextInt(900000)
        String.valueOf(otp)
    }
}

