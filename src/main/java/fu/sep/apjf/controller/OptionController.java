package fu.sep.apjf.controller;

import fu.sep.apjf.dto.request.OptionRequestDto;
import fu.sep.apjf.dto.response.ApiResponseDto;
import fu.sep.apjf.dto.response.OptionResponseDto;
import fu.sep.apjf.service.OptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/options")
@RequiredArgsConstructor
public class OptionController {

    private final OptionService optionService;

    @PostMapping("/question/{questionId}")
    public ResponseEntity<ApiResponseDto<OptionResponseDto>> create(@PathVariable String questionId, @RequestBody OptionRequestDto dto) {
        return ResponseEntity.ok(ApiResponseDto.ok("Tạo đáp án thành công", optionService.createOption(questionId, dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<OptionResponseDto>> update(@PathVariable String id, @RequestBody OptionRequestDto dto) {
        return ResponseEntity.ok(ApiResponseDto.ok("Cập nhật đáp án thành công", optionService.updateOption(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> delete(@PathVariable String id) {
        optionService.deleteOption(id);
        return ResponseEntity.ok(ApiResponseDto.ok("Xóa đáp án thành công", null));
    }

    @GetMapping("/by-question/{questionId}")
    public ResponseEntity<ApiResponseDto<List<OptionResponseDto>>> getByQuestion(@PathVariable String questionId) {
        return ResponseEntity.ok(ApiResponseDto.ok("Danh sách đáp án theo câu hỏi", optionService.getOptionsByQuestionId(questionId)));
    }
}

