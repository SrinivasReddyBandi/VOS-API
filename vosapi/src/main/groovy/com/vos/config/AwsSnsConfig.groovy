package com.vos.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sns.SnsClient

@Configuration
class AwsSnsConfig {
    
    @Value('${aws.region:us-east-1}')
    private String awsRegion
    
    @Bean
    SnsClient snsClient() {
        // Uses DefaultCredentialsProvider which checks in this order:
        // 1. Environment variables (AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY)
        // 2. Java system properties (aws.accessKeyId, aws.secretKey)
        // 3. Web identity token from AWS STS
        // 4. Shared credentials file (~/.aws/credentials)
        // 5. Amazon ECS container credentials
        // 6. Amazon EC2 instance profile credentials
        SnsClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build()
    }
}

