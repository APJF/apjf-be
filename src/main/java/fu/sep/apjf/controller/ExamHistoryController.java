
package fu.sep.apjf.controller;

import fu.sep.apjf.dto.response.ApiResponseDto;
import fu.sep.apjf.dto.response.ExamHistoryDto;
import fu.sep.apjf.entity.EnumClass;
import fu.sep.apjf.service.ExamResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exam-history")
@RequiredArgsConstructor
public class ExamHistoryController {

    private final ExamResultService examResultService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponseDto<List<ExamHistoryDto>>> getUserExamHistory(@PathVariable Long userId) {
        List<ExamHistoryDto> history = examResultService.getExamHistory(userId);
        return ResponseEntity.ok(ApiResponseDto.ok("Lịch sử làm bài kiểm tra", history));
    }

    @GetMapping("/user/{userId}/paginated")
    public ResponseEntity<ApiResponseDto<Page<ExamHistoryDto>>> getUserExamHistoryPaginated(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ExamHistoryDto> history = examResultService.getExamHistory(userId, page, size);
        return ResponseEntity.ok(ApiResponseDto.ok("Lịch sử làm bài kiểm tra phân trang", history));
    }

    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<ApiResponseDto<List<ExamHistoryDto>>> getUserExamHistoryByStatus(
            @PathVariable Long userId,
            @PathVariable EnumClass.ExamStatus status) {
        List<ExamHistoryDto> history = examResultService.getExamHistoryByStatus(userId, status);
        return ResponseEntity.ok(ApiResponseDto.ok("Lịch sử làm bài theo trạng thái", history));
    }

    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<ApiResponseDto<List<ExamHistoryDto>>> getRecentExamHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "5") int limit) {
        List<ExamHistoryDto> history = examResultService.getRecentExamHistory(userId, limit);
        return ResponseEntity.ok(ApiResponseDto.ok("Lịch sử làm bài gần đây", history));
    }

    @GetMapping("/user/{userId}/passed")
    public ResponseEntity<ApiResponseDto<List<ExamHistoryDto>>> getPassedExamHistory(@PathVariable Long userId) {
        List<ExamHistoryDto> history = examResultService.getExamHistoryByStatus(userId, EnumClass.ExamStatus.PASSED);
        return ResponseEntity.ok(ApiResponseDto.ok("Lịch sử bài thi đã đậu", history));
    }

    @GetMapping("/user/{userId}/failed")
    public ResponseEntity<ApiResponseDto<List<ExamHistoryDto>>> getFailedExamHistory(@PathVariable Long userId) {
        List<ExamHistoryDto> history = examResultService.getExamHistoryByStatus(userId, EnumClass.ExamStatus.FAILED);
        return ResponseEntity.ok(ApiResponseDto.ok("Lịch sử bài thi chưa đậu", history));
    }
}
