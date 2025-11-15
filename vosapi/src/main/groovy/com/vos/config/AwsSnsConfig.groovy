package com.vos.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sns.SnsClient

@Configuration
class AwsSnsConfig {

    @Value('${aws.region}')
    private String region

    @Value('${ACCESS_KEY}')
    private String accessKey

    @Value('${SECRET_KEY}')
    private String secretKey

    @Bean
    SnsClient snsClient() {
        def creds = AwsBasicCredentials.create(accessKey, secretKey)

        return SnsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(creds))
                .build()
    }
}

