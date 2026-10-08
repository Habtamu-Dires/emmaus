package com.hab.emmaus.infrastructure.file_manager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
public class S3Config {

    @Value("${s3.space-access-key}")
    String spaceAccessKey;

    @Value("${s3.space-secret-key}")
    String spaceSecretKey;

    @Value("${s3.space-endpoint}")
    String spaceEndPoint;

    @Value("${s3.region}")
    String region;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .credentialsProvider(() -> AwsBasicCredentials.create(
                        spaceAccessKey, spaceSecretKey)
                )
                .endpointOverride(URI.create(spaceEndPoint))
                .region(Region.of(region))
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        try {
            AwsBasicCredentials credentials = AwsBasicCredentials.create(spaceAccessKey, spaceSecretKey);
            return S3Presigner.builder()
                    .credentialsProvider(StaticCredentialsProvider.create(credentials))
                    .endpointOverride(URI.create(spaceEndPoint))
                    .region(Region.of(region))
                    .build();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid S3 presigner configuration: " + e.getMessage(), e);
        }
    }
}
