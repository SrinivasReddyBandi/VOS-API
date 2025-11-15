package com.vos.service.impl

import com.vos.service.NotificationService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sns.model.PublishRequest
import software.amazon.awssdk.services.sns.model.PublishResponse
import software.amazon.awssdk.services.sns.model.SnsException

@Service
class SnsNotificationService implements NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(SnsNotificationService)
    private final SnsClient snsClient
    
    @Value('${aws.sns.topic-arn}')
    private String topicArn
    
    SnsNotificationService(SnsClient snsClient) {
        this.snsClient = snsClient
    }
    
    @Override
    void sendEmail(String to, String subject, String body) {
        try {
            String message = """{"to": "${to}", "subject": "${subject}", "body": "${body.replace('"', '\\"')}"}"""
            
            PublishRequest request = PublishRequest.builder()
                    .topicArn(topicArn)
                    .message(message)
                    .subject(subject)
                    .build()
            
            PublishResponse response = snsClient.publish(request)
            logger.info("Email sent via SNS. MessageId: ${response.messageId()}, To: ${to}")
            
        } catch (SnsException e) {
            logger.error("Error sending email via SNS: ${e.message}", e)
            throw new RuntimeException("Failed to send email via SNS", e)
        }
    }
}

