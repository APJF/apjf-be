package fu.sep.apjf.controller;

import fu.sep.apjf.dto.response.ApiResponseDto;
import fu.sep.apjf.dto.request.QuestionRequestDto;
import fu.sep.apjf.dto.response.QuestionResponseDto;
import fu.sep.apjf.entity.EnumClass;
import fu.sep.apjf.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    /**
     * Lấy danh sách câu hỏi với nhiều tùy chọn lọc, sắp xếp và phân trang
     *
     * @param page      Trang cần hiển thị, bắt đầu từ 0 (mặc định: 0)
     * @param size      Số lượng mục trên mỗi trang (mặc định: 10)
     * @param sort      Trường để sắp xếp (mặc định: "createdAt")
     * @param direction Hướng sắp xếp: "asc" hoặc "desc" (mặc định: "desc")
     * @param keyword   Từ khóa tìm kiếm trong nội dung và tiêu đề câu hỏi
     * @param type      Loại câu hỏi (MULTIPLE_CHOICE, TRUE_FALSE, WRITING)
     * @param scope     Phạm vi của câu hỏi (UNIT, CHAPTER, COURSE)
     * @return Page<QuestionResponseDto> Trang kết quả với các câu hỏi thỏa mãn điều kiện
     */
    @GetMapping
    public ResponseEntity<ApiResponseDto<Page<QuestionResponseDto>>> getAllQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) EnumClass.QuestionType type,
            @RequestParam(required = false) EnumClass.QuestionScope scope) {

        Page<QuestionResponseDto> questions = questionService.getAllQuestions(
                page, size, sort, direction, keyword, type, scope);

        return ResponseEntity.ok(ApiResponseDto.ok("Danh sách câu hỏi", questions));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<QuestionResponseDto>> findById(@PathVariable String id) {
        QuestionResponseDto question = questionService.findById(id);
        return ResponseEntity.ok(ApiResponseDto.ok("Chi tiết câu hỏi", question));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto<QuestionResponseDto>> create(@RequestBody QuestionRequestDto questionDto) {
        QuestionResponseDto question = questionService.createQuestion(questionDto);
        return ResponseEntity.ok(ApiResponseDto.ok("Tạo câu hỏi thành công", question));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<QuestionResponseDto>> update(
            @PathVariable String id,
            @RequestBody QuestionRequestDto questionDto) {
        QuestionResponseDto question = questionService.updateQuestion(id, questionDto);
        return ResponseEntity.ok(ApiResponseDto.ok("Cập nhật câu hỏi thành công", question));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> delete(@PathVariable String id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.ok(ApiResponseDto.ok("Xóa câu hỏi thành công", null));
    }
}
