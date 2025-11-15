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

    @Value('${aws.access-key:AKIA6OLSRFCG3TW2WYFN}')
    private String accessKey

    @Value('${aws.secret-key:QPXzl6tjqX/xyU/whU/RNDFO7MqQ2mz74nuMmap6}')
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

