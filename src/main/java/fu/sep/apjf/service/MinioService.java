package fu.sep.apjf.service;

import java.io.InputStream;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;

@Service
public class MinioService {
    private final MinioClient minioClient;

    @Value("${integration.minio.buckets.avatar}")
    private String avatarBucket;

    @Value("${integration.minio.buckets.document}")
    private String documentBucket;

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    private void createBucketIfNotExists(String bucketName) throws Exception {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    public String uploadAvatar(MultipartFile file, String username) throws Exception {
        createBucketIfNotExists(avatarBucket);
        String objectName = username + "_avatar_" + UUID.randomUUID();
        try (InputStream is = file.getInputStream()) {
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(avatarBucket)
                    .object(objectName)
                    .stream(is, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );
        }
        return objectName;
    }

    public String uploadDocument(MultipartFile file, String username) throws Exception {
        createBucketIfNotExists(documentBucket);
        String objectName = username + "_doc_" + UUID.randomUUID() + ".pdf";
        try (InputStream is = file.getInputStream()) {
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(documentBucket)
                    .object(objectName)
                    .stream(is, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );
        }
        return objectName;
    }
} 