package fu.sep.apjf.controller;

import fu.sep.apjf.dto.CreateExamDto;
import fu.sep.apjf.dto.ExamDto;
import fu.sep.apjf.entity.EnumClass;
import fu.sep.apjf.entity.Exam;
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
        return ResponseEntity.ok(examService.getAllExamsAsDto());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamDto> getExamById(@PathVariable String id) {
        Exam exam = examService.getExamById(id);
        ExamDto examDto = examService.convertToDto(exam);
        return ResponseEntity.ok(examDto);
    }

    @GetMapping("/scope/{scopeType}")
    public ResponseEntity<List<ExamDto>> getExamsByScopeType(@PathVariable EnumClass.ExamScopeType scopeType) {
        List<Exam> exams = examService.getExamsByScopeType(scopeType);
        List<ExamDto> examDtos = exams.stream()
                .map(examService::convertToDto)
                .toList();
        return ResponseEntity.ok(examDtos);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ExamDto>> searchExams(@RequestParam String keyword) {
        List<Exam> exams = examService.searchExams(keyword);
        List<ExamDto> examDtos = exams.stream()
                .map(examService::convertToDto)
                .toList();
        return ResponseEntity.ok(examDtos);
    }

    @PostMapping
    public ResponseEntity<ExamDto> createExam(@RequestBody CreateExamDto createExamDto) {
        Exam exam = examService.createExam(createExamDto);
        ExamDto examDto = examService.convertToDto(exam);
        return ResponseEntity.ok(examDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExamDto> updateExam(@PathVariable String id, @RequestBody CreateExamDto updateExamDto) {
        Exam exam = examService.updateExam(id, updateExamDto);
        ExamDto examDto = examService.convertToDto(exam);
        return ResponseEntity.ok(examDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExam(@PathVariable String id) {
        examService.deleteExam(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{examId}/questions/{questionId}")
    public ResponseEntity<ExamDto> addQuestionToExam(@PathVariable String examId, @PathVariable String questionId) {
        Exam exam = examService.addQuestionToExam(examId, questionId);
        ExamDto examDto = examService.convertToDto(exam);
        return ResponseEntity.ok(examDto);
    }

    @DeleteMapping("/{examId}/questions/{questionId}")
    public ResponseEntity<ExamDto> removeQuestionFromExam(@PathVariable String examId, @PathVariable String questionId) {
        Exam exam = examService.removeQuestionFromExam(examId, questionId);
        ExamDto examDto = examService.convertToDto(exam);
        return ResponseEntity.ok(examDto);
    }
}
