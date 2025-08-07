package fu.sep.apjf.controller;

import fu.sep.apjf.dto.request.ExamResultRequestDto;
import fu.sep.apjf.dto.response.ApiResponseDto;
import fu.sep.apjf.dto.response.ExamOverviewResponseDto;
import fu.sep.apjf.dto.response.ExamResponseDto;
import fu.sep.apjf.dto.response.ExamResultResponseDto;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.service.ExamResultService;
import fu.sep.apjf.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{examId}")
    public ResponseEntity<ApiResponseDto<ExamResponseDto>> getExamDetail(@PathVariable String examId) {
        return ResponseEntity.ok(ApiResponseDto.ok("Chi tiết bài thi", examService.getExamDetail(examId)));
    }

    @PostMapping("/submit")
    public ResponseEntity<ApiResponseDto<ExamResultResponseDto>> submitExam(
            @AuthenticationPrincipal User user,
            @RequestBody ExamResultRequestDto dto) {
        return ResponseEntity.ok(ApiResponseDto.ok("Nộp bài thành công", examResultService.submitExam(user.getId(), dto)));
    }

    @GetMapping("/result/{resultId}")
    public ResponseEntity<ApiResponseDto<ExamResultResponseDto>> getExamResultDetail(@PathVariable Long resultId) {
        return ResponseEntity.ok(ApiResponseDto.ok("Chi tiết kết quả bài thi", examResultService.getExamResult(resultId)));
    }
}

