package fu.sep.apjf.controller;

import fu.sep.apjf.dto.*;
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
    public ResponseEntity<ApiResponse<ExamResultDto>> startExam(@RequestBody StartExamDto startExamDto) {
        ExamResultDto result = examResultService.startExam(startExamDto);
        return ResponseEntity.ok(ApiResponse.ok("Bắt đầu bài thi thành công", result));
    }

    @PostMapping("/exam/{examId}/start")
    public ResponseEntity<ApiResponse<ExamResultDto>> startExamByPath(
            @PathVariable String examId,
            @RequestParam String userId) {
        // Tạo đối tượng StartExamDto mới thay vì sử dụng setter
        StartExamDto startExamDto = new StartExamDto(examId, userId);
        ExamResultDto result = examResultService.startExam(startExamDto);
        return ResponseEntity.ok(ApiResponse.ok("Bắt đầu bài thi thành công", result));
    }

    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<ExamResultDto>> submitExam(
            @RequestBody SubmitExamDto submitExamDto,
            @RequestParam String userId,
            @RequestParam(defaultValue = "false") boolean isAutoSubmit) {
        ExamResultDto result = examResultService.submitExam(submitExamDto, userId, isAutoSubmit);
        String message = isAutoSubmit ? "Bài thi đã được nộp tự động do hết thời gian" : "Nộp bài thi thành công";
        return ResponseEntity.ok(ApiResponse.ok(message, result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExamResultDto>> getExamResultById(@PathVariable String id) {
        ExamResultDto result = examResultService.getExamResultById(id);
        return ResponseEntity.ok(ApiResponse.ok("Thông tin kết quả bài thi", result));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ExamResultDto>>> getExamResultsByUserId(@PathVariable Long userId) {
        List<ExamResultDto> results = examResultService.getExamResultsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.ok("Danh sách kết quả bài thi của người dùng", results));
    }

    @GetMapping("/user/{userId}/passed")
    public ResponseEntity<ApiResponse<List<ExamResultDto>>> getPassedExamsByUser(@PathVariable Long userId) {
        List<ExamResultDto> results = examResultService.getPassedExamsByUser(userId);
        return ResponseEntity.ok(ApiResponse.ok("Danh sách bài thi đã vượt qua", results));
    }

    @GetMapping("/user/{userId}/failed")
    public ResponseEntity<ApiResponse<List<ExamResultDto>>> getFailedExamsByUser(@PathVariable Long userId) {
        List<ExamResultDto> results = examResultService.getFailedExamsByUser(userId);
        return ResponseEntity.ok(ApiResponse.ok("Danh sách bài thi chưa vượt qua", results));
    }

    @GetMapping("/user/{userId}/count")
    public ResponseEntity<ApiResponse<Long>> countCompletedExamsByUser(@PathVariable Long userId) {
        long count = examResultService.countCompletedExamsByUser(userId);
        return ResponseEntity.ok(ApiResponse.ok("Số lượng bài thi đã hoàn thành", count));
    }

    @GetMapping("/user/{userId}/exam/{examId}/exists")
    public ResponseEntity<ApiResponse<Boolean>> hasUserTakenExam(
            @PathVariable Long userId,
            @PathVariable String examId) {
        boolean hasTaken = examResultService.hasUserTakenExam(userId, examId);
        return ResponseEntity.ok(ApiResponse.ok("Trạng thái đã làm bài thi", hasTaken));
    }

    @GetMapping("/exam/{examId}")
    public ResponseEntity<ApiResponse<List<ExamResultDto>>> getExamResultsByExamId(@PathVariable String examId) {
        List<ExamResultDto> results = examResultService.getExamResultsByExamId(examId);
        return ResponseEntity.ok(ApiResponse.ok("Danh sách kết quả của bài thi", results));
    }

    @GetMapping("/exam/{examId}/average-score")
    public ResponseEntity<ApiResponse<Double>> getAverageScoreByExamId(@PathVariable String examId) {
        Double averageScore = examResultService.getAverageScoreByExamId(examId);
        return ResponseEntity.ok(ApiResponse.ok("Điểm trung bình của bài thi", averageScore));
    }

    @GetMapping("/in-progress")
    public ResponseEntity<ApiResponse<List<ExamResultDto>>> getInProgressExams() {
        List<ExamResultDto> results = examResultService.getInProgressExams();
        return ResponseEntity.ok(ApiResponse.ok("Danh sách bài thi đang làm dở", results));
    }
}
