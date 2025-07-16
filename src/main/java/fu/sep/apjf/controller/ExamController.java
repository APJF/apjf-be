package fu.sep.apjf.controller;

import fu.sep.apjf.dto.CreateExamDto;
import fu.sep.apjf.dto.ExamDto;
import fu.sep.apjf.entity.EnumClass;
import fu.sep.apjf.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @GetMapping
    public ResponseEntity<List<ExamDto>> getAllExams() {
        return ResponseEntity.ok(examService.getAllExams());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamDto> getExamById(@PathVariable String id) {
        return ResponseEntity.ok(examService.getExamById(id));
    }

    @GetMapping("/scope/{scopeType}")
    public ResponseEntity<List<ExamDto>> getExamsByScopeType(@PathVariable EnumClass.ExamScopeType scopeType) {
        return ResponseEntity.ok(examService.getExamsByScopeType(scopeType));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ExamDto>> searchExams(@RequestParam String keyword) {
        return ResponseEntity.ok(examService.searchExams(keyword));
    }

    @PostMapping
    public ResponseEntity<ExamDto> createExam(@RequestBody CreateExamDto createExamDto) {
        return ResponseEntity.ok(examService.createExam(createExamDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExamDto> updateExam(@PathVariable String id, @RequestBody CreateExamDto updateExamDto) {
        return ResponseEntity.ok(examService.updateExam(id, updateExamDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExam(@PathVariable String id) {
        examService.deleteExam(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{examId}/questions/{questionId}")
    public ResponseEntity<ExamDto> addQuestionToExam(@PathVariable String examId, @PathVariable String questionId) {
        return ResponseEntity.ok(examService.addQuestionToExam(examId, questionId));
    }

    @DeleteMapping("/{examId}/questions/{questionId}")
    public ResponseEntity<ExamDto> removeQuestionFromExam(@PathVariable String examId, @PathVariable String questionId) {
        return ResponseEntity.ok(examService.removeQuestionFromExam(examId, questionId));
    }
}
