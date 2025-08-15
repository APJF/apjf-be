package fu.sep.apjf.controller;

import fu.sep.apjf.dto.request.ExamResultRequestDto;
import fu.sep.apjf.dto.response.*;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.service.ExamResultService;
import fu.sep.apjf.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/exams")
@RequiredArgsConstructor
public class ExamStudentController {

    private final ExamService examService;
    private final ExamResultService examResultService;

    @GetMapping("/{examId}/overview")
    public ResponseEntity<ApiResponseDto<ExamOverviewResponseDto>> getOverview(@PathVariable String examId) {
        return ResponseEntity.ok(ApiResponseDto.ok("Thông tin tổng quan bài thi", examService.getOverview(examId)));
    }

    @PostMapping("/{examId}/start")
    public ResponseEntity<ApiResponseDto<ExamDetailResponseDto>> startExam(
            @AuthenticationPrincipal User user,
            @PathVariable String examId) {
        return ResponseEntity.ok(ApiResponseDto.ok(
                "Bắt đầu bài thi", examResultService.startExam(user.getId(), examId)));
    }

    @PostMapping("/submit")
    public ResponseEntity<ApiResponseDto<ExamResultResponseDto>> submitExam(
            @AuthenticationPrincipal User user,
            @RequestBody ExamResultRequestDto dto) {
        return ResponseEntity.ok(ApiResponseDto.ok("Nộp bài thành công", examResultService.submitExam(user.getId(), dto)));
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<ExamHistoryResponseDto>>> getHistory(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponseDto.ok("Lịch sử bài thi", examResultService.getHistoryByUserId(user.getId())));
    }

    @GetMapping("/result/{resultId}")
    public ResponseEntity<ApiResponseDto<ExamResultResponseDto>> getExamResultDetail(@PathVariable Long resultId) {
        return ResponseEntity.ok(ApiResponseDto.ok("Chi tiết kết quả bài thi", examResultService.getExamResult(resultId)));
    }
}