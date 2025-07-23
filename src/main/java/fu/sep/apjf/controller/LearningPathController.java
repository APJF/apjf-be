package fu.sep.apjf.controller;

import fu.sep.apjf.dto.*;
import fu.sep.apjf.service.LearningPathService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/learning-paths")
@RequiredArgsConstructor
public class LearningPathController {

    private final LearningPathService learningPathService;

    @PostMapping
    public ResponseEntity<LearningPathDto> create(@RequestBody LearningPathDto dto) {
        return ResponseEntity.ok(learningPathService.createLearningPath(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LearningPathDto> update(@PathVariable Long id, @RequestBody LearningPathDto dto) {
        return ResponseEntity.ok(learningPathService.updateLearningPath(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        learningPathService.deleteLearningPath(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LearningPathDto>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(learningPathService.getLearningPathsByUser(userId));
    }

    @PostMapping("/course")
    public ResponseEntity<Void> addCourse(@RequestBody CourseOrderDto dto) {
        learningPathService.addCourseToLearningPath(dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/course")
    public ResponseEntity<Void> removeCourse(@RequestParam String courseId, @RequestParam Long learningPathId) {
        learningPathService.removeCourseFromLearningPath(courseId, learningPathId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/progress")
    public ResponseEntity<UnitProgressDto> markUnitPassed(@RequestParam String unitId, @RequestParam Long userId) {
        return ResponseEntity.ok(learningPathService.markUnitPassed(unitId, userId));
    }

    /*
    @GetMapping("/chapter-done")
    public ResponseEntity<Boolean> isChapterPassed(@RequestParam Long chapterId, @RequestParam Long userId) {
        return ResponseEntity.ok(learningPathService.isChapterPassed(chapterId, userId));
    }
    */
}
