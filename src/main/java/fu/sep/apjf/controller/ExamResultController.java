package fu.sep.apjf.controller;

import fu.sep.apjf.dto.*;
import fu.sep.apjf.service.ExamResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exam-results")
@RequiredArgsConstructor
public class ExamResultController {

    private final ExamResultService examResultService;

    // Bắt đầu làm bài thi
    @PostMapping("/exams/{examId}/start")
    public ResponseEntity<ApiResponse<ExamResultDto>> startExam(@PathVariable String examId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = getUserIdFromAuth(auth);

        ExamResultDto result = examResultService.startExam(examId, userId);
        return ResponseEntity.ok(ApiResponse.ok("Bắt đầu bài thi thành công", result));
    }

    // Nộp bài thi (đã đơn giản hóa, bỏ isAutoSubmit)
    @PostMapping("/exams/{examId}/submit")
    public ResponseEntity<ApiResponse<ExamResultDto>> submitExam(
            @PathVariable String examId,
            @RequestBody SubmitExamDto submitExamDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = getUserIdFromAuth(auth);

        ExamResultDto result = examResultService.submitExam(examId, submitExamDto, userId);
        return ResponseEntity.ok(ApiResponse.ok("Nộp bài thi thành công", result));
    }

    // Xem kết quả bài thi (review)
    @GetMapping("/exams/{examId}/result")
    public ResponseEntity<ApiResponse<ExamResultDto>> getExamResult(@PathVariable String examId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = getUserIdFromAuth(auth);

        ExamResultDto result = examResultService.getExamResult(examId, userId);
        return ResponseEntity.ok(ApiResponse.ok("Kết quả bài thi", result));
    }

    // Kiểm tra trạng thái bài thi của user (đã làm hay chưa, đang làm dở hay chưa)
    @GetMapping("/exams/{examId}/status")
    public ResponseEntity<ApiResponse<ExamStatusDto>> getExamStatus(@PathVariable String examId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = getUserIdFromAuth(auth);

        ExamStatusDto status = examResultService.getExamStatus(examId, userId);
        return ResponseEntity.ok(ApiResponse.ok("Trạng thái bài thi", status));
    }

    // Helper method để lấy userId từ Authentication
    private String getUserIdFromAuth(Authentication auth) {
        if (auth != null && auth.getPrincipal() instanceof fu.sep.apjf.entity.User) {
            fu.sep.apjf.entity.User user = (fu.sep.apjf.entity.User) auth.getPrincipal();
            return user.getId().toString();
        }
        throw new RuntimeException("Không thể xác định người dùng");
    }
}
