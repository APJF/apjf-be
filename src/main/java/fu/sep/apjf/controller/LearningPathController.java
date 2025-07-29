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

    @PostMapping
    public ResponseEntity<ApiResponseDto<LearningPathResponseDto>> create(@RequestBody LearningPathRequestDto dto) {
        return ResponseEntity.ok(
            ApiResponseDto.ok("Tạo lộ trình học tập thành công", learningPathService.createLearningPath(dto))
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<LearningPathResponseDto>> update(
            @PathVariable Long id,
            @RequestBody LearningPathRequestDto dto) {
        return ResponseEntity.ok(
            ApiResponseDto.ok("Cập nhật lộ trình học tập thành công", learningPathService.updateLearningPath(id, dto))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> delete(@PathVariable Long id) {
        learningPathService.deleteLearningPath(id);
        return ResponseEntity.ok(ApiResponseDto.ok("Xóa lộ trình học tập thành công", null));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponseDto<List<LearningPathResponseDto>>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(
            ApiResponseDto.ok("Danh sách lộ trình học tập theo người dùng", learningPathService.getLearningPathsByUser(userId))
        );
    }

    @PostMapping("/course")
    public ResponseEntity<ApiResponseDto<Void>> addCourse(@RequestBody CourseOrderDto dto) {
        learningPathService.addCourseToLearningPath(dto);
        return ResponseEntity.ok(ApiResponseDto.ok("Thêm khóa học vào lộ trình thành công", null));
    }

    @DeleteMapping("/course")
    public ResponseEntity<ApiResponseDto<Void>> removeCourse(
            @RequestParam String courseId,
            @RequestParam Long learningPathId) {
        learningPathService.removeCourseFromLearningPath(courseId, learningPathId);
        return ResponseEntity.ok(ApiResponseDto.ok("Xóa khóa học khỏi lộ trình thành công", null));
    }
}
