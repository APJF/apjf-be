package fu.sep.apjf.controller;

import fu.sep.apjf.dto.*;
import fu.sep.apjf.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    // Xem chi tiết đề thi (exam detail page)
    @GetMapping("/{examId}")
    public ResponseEntity<ApiResponse<ExamDto>> getExamDetail(@PathVariable String examId) {
        ExamDto exam = examService.getExamDetail(examId);
        return ResponseEntity.ok(ApiResponse.ok("Chi tiết đề thi", exam));
    }
}
