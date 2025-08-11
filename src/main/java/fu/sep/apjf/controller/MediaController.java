package fu.sep.apjf.controller;

import fu.sep.apjf.dto.response.ApiResponseDto;
import fu.sep.apjf.service.MinioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
@Slf4j
public class MediaController {

    private final MinioService minioService;

    @PostMapping("/presign/course-images")
    public ResponseEntity<ApiResponseDto<Map<String, String>>> getCourseImageUrls(
            @Valid @RequestBody CourseImagePresignRequest request) {
        Map<String, String> presignedUrls = minioService.getCourseImageUrls(request.keys());
        return ResponseEntity.ok(ApiResponseDto.ok("Presigned URLs generated successfully", presignedUrls));
    }

    @PostMapping("/presign/avatars")
    public ResponseEntity<ApiResponseDto<Map<String, String>>> getAvatarUrls(
            @Valid @RequestBody AvatarPresignRequest request) {
        Map<String, String> presignedUrls = minioService.getAvatarUrls(request.keys());
        return ResponseEntity.ok(ApiResponseDto.ok("Avatar presigned URLs generated successfully", presignedUrls));
    }

    /**
     * DTO đơn giản cho batch presign course images
     */
    public static record CourseImagePresignRequest(
            @NotEmpty(message = "Keys không được rỗng")
            List<String> keys
    ) {}

    /**
     * DTO đơn giản cho batch presign avatars
     */
    public static record AvatarPresignRequest(
            @NotEmpty(message = "Keys không được rỗng")
            List<String> keys
    ) {}
}
