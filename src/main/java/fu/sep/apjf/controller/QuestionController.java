package fu.sep.apjf.controller;

import fu.sep.apjf.dto.request.OptionRequestDto;
import fu.sep.apjf.dto.request.QuestionRequestDto;
import fu.sep.apjf.dto.response.*;
import fu.sep.apjf.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<ApiResponseDto<PageDto<QuestionWithOptionsResponseDto>>> getAllQuestions(
            @RequestParam(required = false) String questionId,
            @RequestParam(required = false) String unitId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageDto<QuestionWithOptionsResponseDto> questions = questionService.getAllQuestions(questionId,unitId, page, size);
        return ResponseEntity.ok(ApiResponseDto.ok("Lấy danh sách câu hỏi thành công", questions));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<QuestionResponseDto>> getQuestionById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponseDto.ok("Lấy thông tin câu hỏi thành công", questionService.getQuestionById(id)));
    }

    @GetMapping("/{questionId}/options")
    public ResponseEntity<ApiResponseDto<List<OptionResponseDto>>> getByQuestion(@PathVariable String questionId) {
        return ResponseEntity.ok(ApiResponseDto.ok("Danh sách đáp án theo câu hỏi", questionService.getOptionsByQuestionId(questionId)));
    }

    @PostMapping("/{questionId}/options")
    public ResponseEntity<ApiResponseDto<OptionResponseDto>> create(@PathVariable String questionId, @RequestBody OptionRequestDto dto) {
        return ResponseEntity.ok(ApiResponseDto.ok("Tạo đáp án thành công", questionService.createOption(questionId, dto)));
    }
}

