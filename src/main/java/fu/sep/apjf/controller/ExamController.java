package fu.sep.apjf.controller;

import fu.sep.apjf.dto.request.ExamStatusDto;
import fu.sep.apjf.dto.request.SubmitExamDto;
import fu.sep.apjf.dto.response.ApiResponseDto;
import fu.sep.apjf.dto.response.ExamHistoryDto;
import fu.sep.apjf.dto.response.ExamResponseDto;
import fu.sep.apjf.dto.response.ExamResultResponseDto;
import fu.sep.apjf.entity.EnumClass;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.service.ExamResultService;
import fu.sep.apjf.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;
    private final ExamResultService examResultService;

    /**
     * Lấy tất cả bài thi với các tùy chọn phân trang và sắp xếp
     */
    @GetMapping
    public ResponseEntity<ApiResponseDto<Page<ExamResponseDto>>> getAllExams(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String level) {

        Page<ExamResponseDto> exams = examService.getAllExams(
                page, size, sort, direction, status, null, keyword, level);
        return ResponseEntity.ok(ApiResponseDto.ok("Danh sách bài thi", exams));
    }

    /**
     * Lấy danh sách bài thi theo khóa học
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponseDto<Page<ExamResponseDto>>> getExamsByCourse(
            @PathVariable String courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {

        Page<ExamResponseDto> exams = examService.getAllExams(
                page, size, sort, direction, null, courseId, null, null);
        return ResponseEntity.ok(ApiResponseDto.ok("Danh sách bài thi theo khóa học", exams));
    }

    /**
     * Kiểm tra trạng thái bài thi của người dùng
     */
    @GetMapping("/{examId}/status")
    public ResponseEntity<ApiResponseDto<ExamStatusDto>> getExamStatus(
            @PathVariable String examId,
            @AuthenticationPrincipal User user) {

        ExamStatusDto statusDto = examResultService.getExamStatus(examId, user.getId());
        return ResponseEntity.ok(ApiResponseDto.ok("Trạng thái bài thi", statusDto));
    }

    /**
     * Lấy lịch sử làm bài của người dùng
     */
    @GetMapping("/history")
    public ResponseEntity<ApiResponseDto<Page<ExamHistoryDto>>> getExamHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User user) {

        Page<ExamHistoryDto> history = examResultService.getExamHistory(user.getId(), page, size);
        return ResponseEntity.ok(ApiResponseDto.ok("Lịch sử làm bài kiểm tra", history));
    }

    /**
     * Lấy lịch sử làm bài gần đây của người dùng
     */
    @GetMapping("/history/recent")
    public ResponseEntity<ApiResponseDto<Page<ExamHistoryDto>>> getRecentExamHistory(
            @RequestParam(defaultValue = "5") int limit,
            @AuthenticationPrincipal User user) {

        List<ExamHistoryDto> recentHistory = examResultService.getRecentExamHistory(user.getId(), limit);
        Page<ExamHistoryDto> pageResult = Page.empty();
        if (!recentHistory.isEmpty()) {
            pageResult = new org.springframework.data.domain.PageImpl<>(recentHistory);
        }
        return ResponseEntity.ok(ApiResponseDto.ok("Lịch sử làm bài gần đây", pageResult));
    }

    /**
     * Lấy lịch sử làm bài theo trạng thái
     */
    @GetMapping("/history/status/{status}")
    public ResponseEntity<ApiResponseDto<Page<ExamHistoryDto>>> getExamHistoryByStatus(
            @PathVariable EnumClass.ExamStatus status,
            @AuthenticationPrincipal User user) {

        List<ExamHistoryDto> filteredHistory = examResultService.getExamHistoryByStatus(user.getId(), status);
        Page<ExamHistoryDto> pageResult = new org.springframework.data.domain.PageImpl<>(filteredHistory);

        String message = switch (status) {
            case PASSED -> "Lịch sử bài thi đã đậu";
            case FAILED -> "Lịch sử bài thi chưa đậu";
            case IN_PROGRESS -> "Lịch sử bài thi đang làm";
        };

        return ResponseEntity.ok(ApiResponseDto.ok(message, pageResult));
    }

    /**
     * Lấy danh sách bài thi có thể làm
     */
    @GetMapping("/available")
    public ResponseEntity<ApiResponseDto<Page<ExamResponseDto>>> getAvailableExams(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String courseId,
            @AuthenticationPrincipal User user) {

        Page<ExamResponseDto> availableExams = examService.getAvailableExams(
                user.getId(), courseId, page, size, sort, direction);
        return ResponseEntity.ok(ApiResponseDto.ok("Danh sách bài thi có thể làm", availableExams));
    }

    /**
     * Xem chi tiết đề thi
     */
    @GetMapping("/{examId}")
    public ResponseEntity<ApiResponseDto<ExamResponseDto>> getExamDetail(@PathVariable String examId) {
        ExamResponseDto exam = examService.getExamDetail(examId);
        return ResponseEntity.ok(ApiResponseDto.ok("Chi tiết đề thi", exam));
    }

    /**
     * Bắt đầu làm bài thi
     */
    @PostMapping("/{examId}/start")
    public ResponseEntity<ApiResponseDto<ExamResultResponseDto>> startExam(
            @PathVariable String examId,
            @AuthenticationPrincipal User user) {
        ExamResultResponseDto result = examResultService.startExam(examId, user.getId());
        return ResponseEntity.ok(ApiResponseDto.ok("Bắt đầu bài thi thành công", result));
    }

    /**
     * Nộp bài thi
     */
    @PostMapping("/{examId}/submit")
    public ResponseEntity<ApiResponseDto<ExamResultResponseDto>> submitExam(
            @PathVariable String examId,
            @RequestBody SubmitExamDto submitExamDto,
            @AuthenticationPrincipal User user) {
        ExamResultResponseDto result = examResultService.submitExam(examId, submitExamDto, user.getId());
        return ResponseEntity.ok(ApiResponseDto.ok("Nộp bài thi thành công", result));
    }

    /**
     * Xem kết quả chi tiết của bài thi
     */
    @GetMapping("/{examId}/result")
    public ResponseEntity<ApiResponseDto<ExamResultResponseDto>> getExamResult(
            @PathVariable String examId,
            @AuthenticationPrincipal User user) {
        ExamResultResponseDto result = examResultService.getExamResult(examId, user.getId());
        return ResponseEntity.ok(ApiResponseDto.ok("Kết quả bài thi", result));
    }
}
