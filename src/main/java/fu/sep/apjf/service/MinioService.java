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

    public String getAvatarUrl(String objectName) throws Exception {
        if (objectName == null || objectName.trim().isEmpty()) {
            return null;
        }

        // Tạo presigned URL có thời hạn 7 ngày
        return minioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(avatarBucket)
                .object(objectName)
                .expiry(7, TimeUnit.DAYS)
                .build()
        );
    }

    public String getDocumentUrl(String objectName) throws Exception {
        if (objectName == null || objectName.trim().isEmpty()) {
            return null;
        }

        // Nếu objectName đã là URL đầy đủ, trả về luôn
        if (objectName.startsWith("http://") || objectName.startsWith("https://")) {
            return objectName;
        }

        // Tạo presigned URL có thời hạn 24 giờ cho document
        return minioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(documentBucket)
                .object(objectName)
                .expiry(24, TimeUnit.HOURS)
                .build()
        );
    }

    public String getCourseImageUrl(String objectName) throws Exception {
        if (objectName == null || objectName.trim().isEmpty()) {
            return null;
        }

        // Nếu objectName đã là URL đầy đủ, trả về luôn
        if (objectName.startsWith("http://") || objectName.startsWith("https://")) {
            return objectName;
        }

        // Tạo presigned URL có thời hạn 7 ngày cho course image
        return minioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(courseImageBucket)
                .object(objectName)
                .expiry(7, TimeUnit.DAYS)
                .build()
        );
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

    public String uploadCourseImage(MultipartFile file, String courseId) throws Exception {
        createBucketIfNotExists(courseImageBucket);
        String objectName = "course_" + courseId + "_image_" + UUID.randomUUID();
        try (InputStream is = file.getInputStream()) {
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(courseImageBucket)
                    .object(objectName)
                    .stream(is, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );
        }
        return objectName;
    }
}
