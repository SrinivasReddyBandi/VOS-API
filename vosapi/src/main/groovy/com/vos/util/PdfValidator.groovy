package com.vos.util

import com.vos.enums.FileType
import com.vos.enums.ValidationStatus
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class PdfValidator {
    
    private static final Logger logger = LoggerFactory.getLogger(PdfValidator)
    
    ValidationResult validatePdf(InputStream pdfStream, FileType fileType, String fileName) {
        ValidationResult result = new ValidationResult()
        result.validationStatus = ValidationStatus.PENDING
        
        try (PDDocument document = PDDocument.load(pdfStream)) {
            if (document.numberOfPages == 0) {
                result.validationStatus = ValidationStatus.INVALID
                result.validationMessage = "PDF has no pages"
                return result
            }
            
            PDFTextStripper stripper = new PDFTextStripper()
            String text = stripper.getText(document)
            
            if (text == null || text.trim().isEmpty()) {
                result.validationStatus = ValidationStatus.INVALID
                result.validationMessage = "PDF contains no extractable text"
                return result
            }
            
            boolean isValid = validateByFileType(text, fileType)
            
            if (isValid) {
                result.validationStatus = ValidationStatus.VALID
                result.validationMessage = "PDF validation passed"
            } else {
                result.validationStatus = ValidationStatus.INVALID
                result.validationMessage = "PDF content does not match expected format for ${fileType}"
            }
            
        } catch (Exception e) {
            logger.error("Error validating PDF: ${e.message}", e)
            result.validationStatus = ValidationStatus.INVALID
            result.validationMessage = "Error reading PDF: ${e.message}"
        }
        
        result
    }
    
    private boolean validateByFileType(String text, FileType fileType) {
        String lowerText = text.toLowerCase()
        
        switch (fileType) {
            case FileType.BUSINESS_DOC:
                return containsKeywords(lowerText, 
                    "business", "registration", "license", "company", "corporation", "llc", "inc")
            
            case FileType.CONTACT_DOC:
                return containsKeywords(lowerText, 
                    "contact", "address", "phone", "email", "person")
            
            case FileType.BANKING_DOC:
                return containsKeywords(lowerText, 
                    "bank", "account", "routing", "swift", "iban", "financial")
            
            case FileType.COMPLIANCE_DOC:
                return containsKeywords(lowerText, 
                    "compliance", "certificate", "license", "insurance", "tax", "certification")
            
            default:
                return true
        }
    }
    
    private boolean containsKeywords(String text, String... keywords) {
        keywords.any { text.contains(it) }
    }
    
    static class ValidationResult {
        ValidationStatus validationStatus
        String validationMessage
    }
}

