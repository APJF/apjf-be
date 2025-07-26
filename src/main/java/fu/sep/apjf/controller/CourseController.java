package fu.sep.apjf.controller;

import fu.sep.apjf.dto.request.CourseSearchFilter;
import fu.sep.apjf.dto.response.ApiResponseDto;
import fu.sep.apjf.dto.request.CourseRequestDto;
import fu.sep.apjf.dto.response.CourseResponseDto;
import fu.sep.apjf.entity.EnumClass;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Slf4j
public class CourseController {

    private final CourseService courseService;

    /**
     * Lấy danh sách khóa học với nhiều tùy chọn lọc
     * <p>
     * Endpoint này được thiết kế để thay thế cho các endpoint riêng lẻ như:
     * - /api/courses/status/{status}
     * - /api/courses/level/{level}
     * - /api/courses/search?title={title}
     * - /api/courses/entry-level
     * <p>
     * Cách sử dụng:
     * - Lọc theo trạng thái: ?status=PUBLISHED
     * - Lọc theo cấp độ: ?level=N5
     * - Lọc theo tiêu đề: ?title=Tiếng Nhật
     * - Lọc khóa học đầu vào (không có khóa học tiên quyết): ?entryOnly=true
     * - Kết hợp nhiều điều kiện: ?status=PUBLISHED&level=N5&title=Nhật&entryOnly=true
     */
    @GetMapping
    public ResponseEntity<ApiResponseDto<Page<CourseResponseDto>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) EnumClass.Level level,
            @RequestParam(required = false) EnumClass.Status status,
            @RequestParam(required = false, defaultValue = "false") Boolean entryOnly) {

        // Create a filter object from individual parameters
        CourseSearchFilter filter = new CourseSearchFilter(title, level, status, entryOnly);

        return ResponseEntity.ok(
                ApiResponseDto.ok("Danh sách khóa học",
                        courseService.findAll(page, size, sortBy, direction, filter)));
    }

    /* -------- GET /api/courses/{id} -------- */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<CourseResponseDto>> getById(@PathVariable String id) {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Chi tiết khóa học", courseService.findById(id)));
    }

    /* -------- POST /api/courses -------- */
    @PostMapping
    public ResponseEntity<ApiResponseDto<CourseResponseDto>> create(
            @Valid @RequestBody CourseRequestDto dto,
            @AuthenticationPrincipal User user) {

        log.info("Staff {} đang tạo khóa học mới: {}", user.getUsername(), dto.title());

        CourseResponseDto created = courseService.create(dto, user.getId());
        return ResponseEntity.created(URI.create("/api/courses/" + created.id()))
                .body(ApiResponseDto.ok("Tạo khóa học thành công", created));
    }

    /* -------- PUT /api/courses/{id} -------- */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<CourseResponseDto>> update(
            @PathVariable String id,
            @Valid @RequestBody CourseRequestDto dto,
            @AuthenticationPrincipal User user) {

        log.info("Staff {} đang cập nhật khóa học: {}", user.getUsername(), id);

        return ResponseEntity.ok(
                ApiResponseDto.ok("Cập nhật khóa học thành công", courseService.update(id, dto, user.getId())));
    }
}