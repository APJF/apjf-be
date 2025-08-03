package fu.sep.apjf.service;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;

@Service
public class MinioService {
    private final MinioClient minioClient;

    @Value("${integration.minio.buckets.avatar}")
    private String avatarBucket;

    @Value("${integration.minio.buckets.document}")
    private String documentBucket;

    @Value("${integration.minio.buckets.course-image:course-image}")
    private String courseImageBucket;

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    private void createBucketIfNotExists(String bucketName) throws Exception {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    // Helper method để tránh duplicate code
    private void uploadFile(String bucket, String objectName, MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream()) {
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .stream(is, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );
        }
    }

    // Optimize uploadAvatar method
    public String uploadAvatar(MultipartFile file, String username) throws Exception {
        createBucketIfNotExists(avatarBucket);
        String objectName = username + "_avatar_" + UUID.randomUUID();
        uploadFile(avatarBucket, objectName, file);
        return objectName;
    }

    // Optimize getAvatarUrl method
    public String getAvatarUrl(String objectName) throws Exception {
        return getPresignedUrl(avatarBucket, objectName, 7, TimeUnit.DAYS);
    }

    // Optimize uploadDocument method
    public String uploadDocument(MultipartFile file, String username) throws Exception {
        createBucketIfNotExists(documentBucket);
        String objectName = username + "_doc_" + UUID.randomUUID() + ".pdf";
        uploadFile(documentBucket, objectName, file);
        return objectName;
    }

    // Optimize getDocumentUrl method
    public String getDocumentUrl(String objectName) throws Exception {
        return getPresignedUrl(documentBucket, objectName, 24, TimeUnit.HOURS);
    }

    public String uploadCourseImage(MultipartFile file) throws Exception {
        createBucketIfNotExists(courseImageBucket);
        String objectName = "course_image_" + UUID.randomUUID();
        uploadFile(courseImageBucket, objectName, file);
        return objectName;
    }

    // Optimize getCourseImageUrl method
    public String getCourseImageUrl(String objectName) throws Exception {
        return getPresignedUrl(courseImageBucket, objectName, 7, TimeUnit.DAYS);
    }

    // Helper method để tránh duplicate code cho presigned URL
    private String getPresignedUrl(String bucket, String objectName, int expiry, TimeUnit unit) throws Exception {
        if (objectName == null || objectName.trim().isEmpty()) {
            return null;
        }

        // Nếu objectName đã là URL đầy đủ, trả về luôn
        if (objectName.startsWith("http://") || objectName.startsWith("https://")) {
            return objectName;
        }

        return minioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(bucket)
                .object(objectName)
                .expiry(expiry, unit)
                .build()
        );
    }
}
