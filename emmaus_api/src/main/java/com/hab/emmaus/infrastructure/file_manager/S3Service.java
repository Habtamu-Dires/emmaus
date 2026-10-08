package com.hab.emmaus.infrastructure.file_manager;

import com.hab.emmaus.infrastructure.file_manager.dto.GeneratePresignedUrlRequest;
import com.hab.emmaus.infrastructure.file_manager.dto.PreSignedUrlDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.*;

import java.net.URI;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${s3.bucket-name}")
    String bucketName;
    @Value("${s3.region}")
    String region;


    public PreSignedUrlDto generatePresignedUrl(GeneratePresignedUrlRequest req) {
        try {

            String key = req.folder() + "/" + req.fileName();

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(req.contentType())
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .putObjectRequest(putObjectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

            // Generate public URL BEFORE upload
            String publicUrl = String.format("https://%s.%s.digitaloceanspaces.com/%s",
                    bucketName, region, key);

            return new PreSignedUrlDto(
                    presignedRequest.url().toString(),
                    publicUrl,
                    key
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate pre-signed URL: " + e.getMessage(), e);
        }
    }


    // New DELETE presigned URL endpoint
    public PreSignedUrlDto getPresignedUrlToDelete(String fileName, String folder) {
        try {
            String key  = folder + "/" + fileName;

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            DeleteObjectPresignRequest presignRequest = DeleteObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .deleteObjectRequest(deleteObjectRequest)
                    .build();

            PresignedDeleteObjectRequest presignedRequest = s3Presigner.presignDeleteObject(presignRequest);

            // Generate public URL BEFORE upload
            String publicUrl = String.format("https://%s.%s.digitaloceanspaces.com/%s",
                    bucketName, region, key);

            return new PreSignedUrlDto(
                    presignedRequest.url().toString(),
                    publicUrl,
                    key
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate pre-signed DELETE URL: " + e.getMessage(), e);
        }
    }

    @Retryable
    public void deleteFile(String publicUrl) {
        if (publicUrl == null || publicUrl.isBlank()) {
            return;
        }

        try {
            URI uri = URI.create(publicUrl);

            String key = uri.getPath();

            // Remove leading /
            if (key.startsWith("/")) {
                key = key.substring(1);
            }

            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteRequest);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to delete file: " + publicUrl, e
            );
        }
    }


}
