package fu.sep.apjf.controller;

import fu.sep.apjf.dto.request.LearningPathRequestDto;
import fu.sep.apjf.dto.response.ApiResponseDto;
import fu.sep.apjf.dto.response.CourseOrderDto;
import fu.sep.apjf.dto.response.LearningPathDetailResponseDto;
import fu.sep.apjf.dto.response.LearningPathResponseDto;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.service.LearningPathService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/learning-paths")
@RequiredArgsConstructor
public class LearningPathController {

    private final LearningPathService learningPathService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<LearningPathDetailResponseDto>>> getUserLearningPaths(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponseDto.ok(
            "Danh sách lộ trình học",
            learningPathService.getLearningPathsByUser(user.getId())
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<LearningPathDetailResponseDto>> getLearningPathById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponseDto.ok(
            "Chi tiết lộ trình học",
            learningPathService.getLearningPathById(id,user)
        ));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto<LearningPathResponseDto>> createLearningPath(
            @Valid @RequestBody LearningPathRequestDto dto,
            @AuthenticationPrincipal User user) {
        LearningPathResponseDto created = learningPathService.createLearningPath(dto, user.getId());
        return ResponseEntity.ok(ApiResponseDto.ok("Tạo lộ trình học thành công", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<LearningPathResponseDto>> updateLearningPath(
            @PathVariable Long id,
            @Valid @RequestBody LearningPathRequestDto dto,
            @AuthenticationPrincipal User user) {
        LearningPathResponseDto updated = learningPathService.updateLearningPath(id, dto, user.getId());
        return ResponseEntity.ok(ApiResponseDto.ok("Cập nhật lộ trình học thành công", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<String>> deleteLearningPath(@PathVariable Long id) {
        learningPathService.deleteLearningPath(id);
        return ResponseEntity.ok(ApiResponseDto.ok("Xóa lộ trình học thành công"));
    }

    @PutMapping("/{id}/active")
    public ResponseEntity<ApiResponseDto<String>> setActiveLearningPath(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        learningPathService.setStudyingLearningPath(user.getId(), id);
        return ResponseEntity.ok(ApiResponseDto.ok("Đặt lộ trình học hoạt động thành công"));
    }

    @PutMapping("/{id}/reorder")
    public ResponseEntity<ApiResponseDto<String>> reorderCourses(
            @PathVariable Long id,
            @RequestBody List<String> courseIds) {
        learningPathService.reorderCoursesInPath(id, courseIds);
        return ResponseEntity.ok(ApiResponseDto.ok("Sắp xếp lại khóa học thành công"));
    }

    @PostMapping("/{id}/courses")
    public ResponseEntity<ApiResponseDto<String>> addCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseOrderDto dto) {
        learningPathService.addCourseToLearningPath(id, dto);
        return ResponseEntity.ok(ApiResponseDto.ok("Thêm khóa học vào lộ trình thành công"));
    }

    @DeleteMapping("/{id}/courses/{courseId}")
    public ResponseEntity<ApiResponseDto<String>> removeCourse(
            @PathVariable Long id,
            @PathVariable String courseId) {
        learningPathService.removeCourseFromLearningPath(courseId, id);
        return ResponseEntity.ok(ApiResponseDto.ok("Xóa khóa học khỏi lộ trình học thành công"));
    }
}
