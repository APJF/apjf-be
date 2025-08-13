package fu.sep.apjf.controller;

import fu.sep.apjf.dto.request.ExamRequestDto;
import fu.sep.apjf.dto.response.ApiResponseDto;
import fu.sep.apjf.dto.response.ExamResponseDto;
import fu.sep.apjf.dto.response.ExamListResponseDto;
import fu.sep.apjf.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @PostMapping
    public ResponseEntity<ApiResponseDto<ExamResponseDto>> create(@RequestBody ExamRequestDto dto) {
        ExamResponseDto created = examService.create(dto);
        return ResponseEntity.created(URI.create("/api/exams/" + created.id()))
                .body(ApiResponseDto.ok("Tạo bài thi thành công", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<ExamResponseDto>> update(@PathVariable String id, @RequestBody ExamRequestDto dto) {
        return ResponseEntity.ok(ApiResponseDto.ok("Cập nhật bài thi thành công", examService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> delete(@PathVariable String id) {
        examService.delete(id);
        return ResponseEntity.ok(ApiResponseDto.ok("Xóa bài thi thành công", null));
    }

    @PostMapping("/{examId}/questions")
    public ResponseEntity<ApiResponseDto<Void>> addQuestions(@PathVariable String examId, @RequestBody List<String> questionIds) {
        examService.addQuestions(examId, questionIds);
        return ResponseEntity.ok(ApiResponseDto.ok("Thêm danh sách câu hỏi vào bài thi thành công", null));
    }

    @DeleteMapping("/{examId}/questions")
    public ResponseEntity<ApiResponseDto<Void>> removeQuestions(@PathVariable String examId, @RequestBody List<String> questionIds) {
        examService.removeQuestions(examId, questionIds);
        return ResponseEntity.ok(ApiResponseDto.ok("Xóa danh sách câu hỏi khỏi bài thi thành công", null));
    }

    @GetMapping("/{examId}")
    public ResponseEntity<ApiResponseDto<ExamResponseDto>> getExamDetail(@PathVariable String examId) {
        return ResponseEntity.ok(ApiResponseDto.ok("Chi tiết bài thi", examService.getExamDetail(examId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<ExamListResponseDto>>> getAll() {
        return ResponseEntity.ok(ApiResponseDto.ok("Danh sách tất cả bài thi", examService.findAll()));
    }

    @GetMapping("/{examId}/questions")
    public ResponseEntity<ApiResponseDto<List<fu.sep.apjf.dto.response.QuestionResponseDto>>> getQuestionsByExam(@PathVariable String examId) {
        return ResponseEntity.ok(ApiResponseDto.ok("Danh sách câu hỏi theo bài thi", examService.getQuestionsByExamId(examId)));
    }
}
