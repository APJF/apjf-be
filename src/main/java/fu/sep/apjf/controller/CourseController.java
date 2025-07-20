package fu.sep.apjf.controller;

import fu.sep.apjf.dto.ApiResponse;
import fu.sep.apjf.dto.CourseDetailDto;
import fu.sep.apjf.dto.CourseDto;
import fu.sep.apjf.entity.EnumClass;
import fu.sep.apjf.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    /* -------- GET /api/courses -------- */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<CourseDto>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) EnumClass.Level level,
            @RequestParam(required = false) EnumClass.Status status) {
        return ResponseEntity.ok(
                ApiResponse.ok("Danh sách khóa học",
                        courseService.findAll(page, size, sortBy, direction, title, level, status)));
    }

    /* -------- GET /api/courses/{id} -------- */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseDto>> getById(@PathVariable String id) {
        return ResponseEntity.ok(
                ApiResponse.ok("Chi tiết khóa học", courseService.findById(id)));
    }

    /* -------- GET /api/courses/{id}/detail -------- */
    @GetMapping("/{id}/detail")
    public ResponseEntity<ApiResponse<CourseDetailDto>> getFullDetail(@PathVariable String id) {
        return ResponseEntity.ok(
                ApiResponse.ok("Khóa học với chương và bài học", courseService.findDetail(id)));
    }

    /* -------- NEW ENDPOINTS USING UPDATED SERVICE -------- */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<Page<CourseDto>>> getCoursesByStatus(
            @PathVariable EnumClass.Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(
                ApiResponse.ok("Khóa học theo trạng thái",
                        courseService.findByStatus(status, page, size)));
    }

    @GetMapping("/level/{level}")
    public ResponseEntity<ApiResponse<Page<CourseDto>>> getCoursesByLevel(
            @PathVariable EnumClass.Level level,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(
                ApiResponse.ok("Khóa học theo cấp độ",
                        courseService.findByLevel(level, page, size)));
    }

    @GetMapping("/entry-level")
    public ResponseEntity<ApiResponse<List<CourseDto>>> getEntryLevelCourses() {
        return ResponseEntity.ok(
                ApiResponse.ok("Khóa học đầu vào", courseService.findEntryLevelCourses()));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<CourseDto>>> searchCourses(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(
                ApiResponse.ok("Kết quả tìm kiếm khóa học",
                        courseService.searchByTitle(title, page, size)));
    }

    @GetMapping("/published/level/{level}")
    public ResponseEntity<ApiResponse<List<CourseDto>>> getPublishedCoursesByLevel(
            @PathVariable EnumClass.Level level) {
        return ResponseEntity.ok(
                ApiResponse.ok("Khóa học đã xuất bản theo cấp độ",
                        courseService.findPublishedCoursesByLevel(level)));
    }

    @GetMapping("/stats/count-by-status/{status}")
    public ResponseEntity<ApiResponse<Long>> countCoursesByStatus(@PathVariable EnumClass.Status status) {
        return ResponseEntity.ok(
                ApiResponse.ok("Số lượng khóa học theo trạng thái",
                        courseService.countByStatus(status)));
    }

    @GetMapping("/stats/count-by-level/{level}")
    public ResponseEntity<ApiResponse<Long>> countCoursesByLevel(@PathVariable EnumClass.Level level) {
        return ResponseEntity.ok(
                ApiResponse.ok("Số lượng khóa học theo cấp độ",
                        courseService.countByLevel(level)));
    }

    @GetMapping("/exists/title/{title}")
    public ResponseEntity<ApiResponse<Boolean>> checkTitleExists(@PathVariable String title) {
        return ResponseEntity.ok(
                ApiResponse.ok("Kiểm tra tên khóa học tồn tại",
                        courseService.existsByTitle(title)));
    }

    /* -------- POST /api/courses -------- */
    @PostMapping
    public ResponseEntity<ApiResponse<CourseDto>> create(@Valid @RequestBody CourseDto dto,
                                                         @RequestHeader("X-User-Id") String staffId) {
        CourseDto created = courseService.create(dto, staffId);
        return ResponseEntity.created(URI.create("/api/courses/" + created.id()))
                .body(ApiResponse.ok("Tạo khóa học thành công", created));
    }

    /* -------- PUT /api/courses/{id} -------- */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseDto>> update(@PathVariable String id,
                                                         @Valid @RequestBody CourseDto dto,
                                                         @RequestHeader("X-User-Id") String staffId) {
        return ResponseEntity.ok(
                ApiResponse.ok("Cập nhật khóa học thành công", courseService.update(id, dto, staffId)));
    }
}