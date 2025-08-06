package fu.sep.apjf.controller;

import fu.sep.apjf.dto.request.QuestionRequestDto;
import fu.sep.apjf.dto.response.ApiResponseDto;
import fu.sep.apjf.dto.response.QuestionResponseDto;
import fu.sep.apjf.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    public ResponseEntity<ApiResponseDto<QuestionResponseDto>> create(@RequestBody QuestionRequestDto dto) {
        return ResponseEntity.ok(ApiResponseDto.ok("Tạo câu hỏi thành công", questionService.createQuestion(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<QuestionResponseDto>> update(@PathVariable String id, @RequestBody QuestionRequestDto dto) {
        return ResponseEntity.ok(ApiResponseDto.ok("Cập nhật câu hỏi thành công", questionService.updateQuestion(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> delete(@PathVariable String id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.ok(ApiResponseDto.ok("Xóa câu hỏi thành công", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<QuestionResponseDto>>> getAllQuestions() {
        return ResponseEntity.ok(ApiResponseDto.ok("Lấy danh sách câu hỏi thành công", questionService.getAllQuestions()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<QuestionResponseDto>> getQuestionById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponseDto.ok("Lấy thông tin câu hỏi thành công", questionService.getQuestionById(id)));
    }
}

