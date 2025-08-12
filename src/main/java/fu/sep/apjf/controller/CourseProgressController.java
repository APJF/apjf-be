package fu.sep.apjf.controller;

import fu.sep.apjf.dto.request.CourseProgressRequestDto;
import fu.sep.apjf.dto.response.ApiResponseDto;
import fu.sep.apjf.dto.response.CourseProgressResponseDto;
import fu.sep.apjf.entity.CourseProgressKey;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.service.CourseProgressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@RestController
@RequestMapping("/api/course-progress")
@RequiredArgsConstructor
@Slf4j
public class CourseProgressController {

    private final CourseProgressService courseProgressService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponseDto<List<CourseProgressResponseDto>>> getByUserId(
            @PathVariable Long userId) {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Danh sách tiến trình khóa học của user",
                        courseProgressService.findByUserId(userId))
        );
    }

    @GetMapping("/{courseId}/detail")
    public ResponseEntity<ApiResponseDto<CourseProgressResponseDto>> getDetail(
            @PathVariable String courseId,
            @AuthenticationPrincipal User user) {
        CourseProgressKey key = new CourseProgressKey(courseId, user.getId());
        return ResponseEntity.ok(
                ApiResponseDto.ok("Chi tiết tiến trình khóa học",
                        courseProgressService.findById(key))
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto<CourseProgressResponseDto>> create(
            @Validated @RequestBody CourseProgressRequestDto dto) {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Tạo tiến trình khóa học thành công",
                        courseProgressService.create(dto))
        );
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<ApiResponseDto<CourseProgressResponseDto>> update(
            @PathVariable String courseId,
            @AuthenticationPrincipal User user,
            @Validated @RequestBody CourseProgressRequestDto dto) {
        CourseProgressKey key = new CourseProgressKey(courseId, user.getId());
        return ResponseEntity.ok(
                ApiResponseDto.ok("Cập nhật tiến trình khóa học thành công",
                        courseProgressService.update(key, dto))
        );
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<ApiResponseDto<Void>> delete(
            @PathVariable String courseId,
            @AuthenticationPrincipal User user) {
        CourseProgressKey key = new CourseProgressKey(courseId, user.getId());
        courseProgressService.delete(key);
        return ResponseEntity.ok(
                ApiResponseDto.ok("Xóa tiến trình khóa học thành công", null)
        );
    }
}
