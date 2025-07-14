package fu.sep.apjf.controller;

import fu.sep.apjf.dto.*;
import fu.sep.apjf.entity.ExamResult;
import fu.sep.apjf.service.ExamResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exam-results")
@RequiredArgsConstructor
public class ExamResultController {

    private final ExamResultService examResultService;

    @PostMapping("/start")
    public ResponseEntity<ExamResult> startExam(@RequestBody StartExamDto startExamDto) {
        ExamResult examResult = examResultService.startExam(startExamDto);
        return ResponseEntity.ok(examResult);
    }

    @PostMapping("/exam/{examId}/start")
    public ResponseEntity<ExamResult> startExamByPath(@PathVariable String examId, @RequestParam String userId) {
        StartExamDto startExamDto = new StartExamDto();
        startExamDto.setExamId(examId);
        startExamDto.setUserId(userId);

        ExamResult examResult = examResultService.startExam(startExamDto);
        return ResponseEntity.ok(examResult);
    }

    @PostMapping("/submit")
    public ResponseEntity<ExamResult> submitExam(@RequestBody SubmitExamDto submitExamDto) {
        ExamResult examResult = examResultService.submitExam(submitExamDto);
        return ResponseEntity.ok(examResult);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamResult> getExamResultById(@PathVariable String id) {
        ExamResult examResult = examResultService.getExamResultById(id);
        return ResponseEntity.ok(examResult);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExamResult>> getExamResultsByUserId(@PathVariable Long userId) {
        List<ExamResult> examResults = examResultService.getExamResultsByUserId(userId);
        return ResponseEntity.ok(examResults);
    }

    @GetMapping("/user/{userId}/passed")
    public ResponseEntity<List<ExamResult>> getPassedExamsByUser(@PathVariable Long userId) {
        List<ExamResult> examResults = examResultService.getPassedExamsByUser(userId);
        return ResponseEntity.ok(examResults);
    }

    @GetMapping("/user/{userId}/failed")
    public ResponseEntity<List<ExamResult>> getFailedExamsByUser(@PathVariable Long userId) {
        List<ExamResult> examResults = examResultService.getFailedExamsByUser(userId);
        return ResponseEntity.ok(examResults);
    }

    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Long> countCompletedExamsByUser(@PathVariable Long userId) {
        long count = examResultService.countCompletedExamsByUser(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/user/{userId}/exam/{examId}/exists")
    public ResponseEntity<Boolean> hasUserTakenExam(@PathVariable Long userId, @PathVariable String examId) {
        boolean hasTaken = examResultService.hasUserTakenExam(userId, examId);
        return ResponseEntity.ok(hasTaken);
    }

    @GetMapping("/exam/{examId}")
    public ResponseEntity<List<ExamResult>> getExamResultsByExamId(@PathVariable String examId) {
        List<ExamResult> examResults = examResultService.getExamResultsByExamId(examId);
        return ResponseEntity.ok(examResults);
    }

    @GetMapping("/exam/{examId}/average-score")
    public ResponseEntity<Double> getAverageScoreByExamId(@PathVariable String examId) {
        Double averageScore = examResultService.getAverageScoreByExamId(examId);
        return ResponseEntity.ok(averageScore);
    }

    @GetMapping("/in-progress")
    public ResponseEntity<List<ExamResult>> getInProgressExams() {
        List<ExamResult> inProgressExams = examResultService.getInProgressExams();
        return ResponseEntity.ok(inProgressExams);
    }
}
