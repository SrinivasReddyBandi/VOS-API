package com.vos.util

import org.springframework.stereotype.Component

@Component
class StringSanitizer {
    
    String sanitize(String input) {
        if (input == null) return null
        input.trim()
            .replaceAll('<', '&lt;')
            .replaceAll('>', '&gt;')
            .replaceAll('"', '&quot;')
            .replaceAll("'", '&#x27;')
            .replaceAll('/', '&#x2F;')
    }
    
    String sanitizeForLogging(String input) {
        if (input == null) return null
        input.replaceAll(/\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b/, '[EMAIL]')
            .replaceAll(/\\b\\d{10,}\\b/, '[NUMBER]')
    }
}

