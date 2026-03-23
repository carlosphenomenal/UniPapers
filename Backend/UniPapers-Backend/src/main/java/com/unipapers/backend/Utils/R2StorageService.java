package com.unipapers.backend.Utils;

import com.unipapers.backend.Configurations.Cloudflare.R2Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class R2StorageService {

    private final S3Client s3;
    private final R2Properties props;

    /** Upload a file from a MultipartFile */
    public String upload(MultipartFile file, String key) throws IOException {
        s3.putObject(
                PutObjectRequest.builder()
                        .bucket(props.getBucketName())
                        .key(key)
                        .contentType(file.getContentType())
                        .contentLength(file.getSize())
                        .build(),
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
        );

        // Return public URL if the bucket is public, otherwise return the key
        return props.getPublicUrl() != null
                ? props.getPublicUrl() + "/" + key
                : key;
    }

    /** <p>Instead of the frontend sending the file to the server and then the server to the
     * bucket (which tends to slow down the process), we can just send the file to the bucket directly from the
     * frontend using a pre-signed URL.
     * <p>This method generates a pre-signed URL that the frontend will for uploading the file to the bucket.
     * */
    public String presignedUploadUrl(String key, String contentType, Duration expiry) {
        try (S3Presigner presigner = S3Presigner.builder()
                .endpointOverride(URI.create(
                        "https://" + props.getAccountId() + ".r2.cloudflarestorage.com"
                ))
                .region(Region.of("auto"))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey())
                ))
                .build()) {

            PresignedPutObjectRequest presigned = presigner.presignPutObject(r ->
                    r.signatureDuration(expiry)
                            .putObjectRequest(p -> p
                                    .bucket(props.getBucketName())
                                    .key(key)
                                    .contentType(contentType)
                            )
            );

            return presigned.url().toString();
        }
    }

    /** Download a file as bytes */
    public byte[] download(String key) {
        ResponseBytes<GetObjectResponse> response = s3.getObjectAsBytes(
                GetObjectRequest.builder()
                        .bucket(props.getBucketName())
                        .key(key)
                        .build()
        );
        return response.asByteArray();
    }

    /** Delete a file */
    public void delete(String key) {
        s3.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(props.getBucketName())
                        .key(key)
                        .build()
        );
    }

    /** Generate a pre-signed URL (for private buckets) */
    public URL presignedUrl(String key, Duration expiry) {
        try (S3Presigner presigner = S3Presigner.builder()
                .endpointOverride(URI.create(
                        "https://" + props.getAccountId() + ".r2.cloudflarestorage.com"
                ))
                .region(Region.of("auto"))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey())
                ))
                .build()) {

            PresignedGetObjectRequest presigned = presigner.presignGetObject(r ->
                    r.signatureDuration(expiry)
                            .getObjectRequest(g -> g.bucket(props.getBucketName()).key(key))
            );

            return presigned.url();
        }
    }

    /** List all objects in the bucket */
    public List<String> listKeys(String prefix) {
        ListObjectsV2Response response = s3.listObjectsV2(
                ListObjectsV2Request.builder()
                        .bucket(props.getBucketName())
                        .prefix(prefix)
                        .build()
        );
        return response.contents().stream()
                .map(S3Object::key)
                .toList();
    }
}
