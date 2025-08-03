package fu.sep.apjf.controller;

import fu.sep.apjf.dto.request.LearningPathRequestDto;
import fu.sep.apjf.dto.response.ApiResponseDto;
import fu.sep.apjf.dto.response.CourseOrderDto;
import fu.sep.apjf.dto.response.LearningPathResponseDto;
import fu.sep.apjf.service.LearningPathService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/learning-paths")
@RequiredArgsConstructor
public class LearningPathController {

    private final LearningPathService learningPathService;

    // Tạo lộ trình học
    @PostMapping
    public ResponseEntity<ApiResponseDto<LearningPathResponseDto>> create(@RequestBody LearningPathRequestDto dto) {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Tạo lộ trình học thành công", learningPathService.createLearningPath(dto))
        );
    }

    // Cập nhật lộ trình học
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<LearningPathResponseDto>> update(
            @PathVariable Long id,
            @RequestBody LearningPathRequestDto dto) {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Cập nhật lộ trình học thành công", learningPathService.updateLearningPath(id, dto))
        );
    }

    // Xóa lộ trình học
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> delete(@PathVariable Long id) {
        learningPathService.deleteLearningPath(id);
        return ResponseEntity.ok(ApiResponseDto.ok("Xóa lộ trình học thành công", null));
    }

    // Lấy tất cả lộ trình của 1 người dùng
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponseDto<List<LearningPathResponseDto>>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Danh sách lộ trình học của người dùng", learningPathService.getLearningPathsByUser(userId))
        );
    }

    // Lấy lộ trình đang được theo dõi (status = STUDYING)
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<ApiResponseDto<LearningPathResponseDto>> getActive(@PathVariable Long userId) {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Lộ trình học hiện tại đang theo dõi", learningPathService.getActiveLearningPath(userId))
        );
    }

    // Đặt lộ trình học là đang theo dõi (STUDYING)
    @PutMapping("/{learningPathId}/active")
    public ResponseEntity<ApiResponseDto<Void>> setActive(
            @PathVariable Long learningPathId,
            @RequestParam Long userId) {
        learningPathService.setActiveLearningPath(userId, learningPathId);
        return ResponseEntity.ok(ApiResponseDto.ok("Đã chọn lộ trình học để theo dõi", null));
    }

    // Thêm khóa học vào lộ trình
    @PostMapping("/course")
    public ResponseEntity<ApiResponseDto<Void>> addCourse(@RequestBody CourseOrderDto dto) {
        learningPathService.addCourseToLearningPath(dto);
        return ResponseEntity.ok(ApiResponseDto.ok("Thêm khóa học vào lộ trình thành công", null));
    }

    // Xóa khóa học khỏi lộ trình
    @DeleteMapping("/course")
    public ResponseEntity<ApiResponseDto<Void>> removeCourse(
            @RequestParam String courseId,
            @RequestParam Long learningPathId) {
        learningPathService.removeCourseFromLearningPath(courseId, learningPathId);
        return ResponseEntity.ok(ApiResponseDto.ok("Xóa khóa học khỏi lộ trình thành công", null));
    }
}
