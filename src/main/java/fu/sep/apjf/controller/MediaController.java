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

    @PostMapping("/presign")
    public ResponseEntity<ApiResponseDto<Map<String, String>>> getPresignedUrls(
            @Valid @RequestBody PresignRequest request) {
        Map<String, String> presignedUrls = minioService.getPresignedUrls(request.keys());
        return ResponseEntity.ok(ApiResponseDto.ok("Presigned URLs generated successfully", presignedUrls));
    }

    /**
     * DTO đơn giản cho presign - không cần type
     */
    public static record PresignRequest(
            @NotEmpty(message = "Keys không được rỗng")
            List<String> keys
    ) {}
}
