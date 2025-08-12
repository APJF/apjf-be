package fu.sep.apjf.controller;

import fu.sep.apjf.dto.request.LearningPathProgressRequestDto;
import fu.sep.apjf.dto.response.ApiResponseDto;
import fu.sep.apjf.dto.response.LearningPathProgressDetailResponseDto;
import fu.sep.apjf.dto.response.LearningPathProgressOverviewDto;
import fu.sep.apjf.entity.LearningPathProgressKey;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.service.LearningPathProgressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@RestController
@RequestMapping("/api/learning-path-progress")
@RequiredArgsConstructor
@Slf4j
public class LearningPathProgressController {

    private final LearningPathProgressService learningPathProgressService;

    @GetMapping("/user/{userId}/overview")
    public ResponseEntity<ApiResponseDto<List<LearningPathProgressOverviewDto>>> getOverviewByUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Danh sách tiến trình learning path (overview)",
                        learningPathProgressService.getOverviewByUser(userId))
        );
    }

    @GetMapping("/{learningPathId}/detail")
    public ResponseEntity<ApiResponseDto<LearningPathProgressDetailResponseDto>> getDetail(
            @PathVariable Long learningPathId,
            @AuthenticationPrincipal User user) {
        LearningPathProgressKey key = new LearningPathProgressKey(learningPathId, user.getId());
        return ResponseEntity.ok(
                ApiResponseDto.ok("Chi tiết tiến trình learning path",
                        learningPathProgressService.getDetail(key))
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto<LearningPathProgressDetailResponseDto>> create(
            @Validated @RequestBody LearningPathProgressRequestDto dto) {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Tạo tiến trình learning path thành công",
                        learningPathProgressService.create(dto))
        );
    }

    @PutMapping("/{learningPathId}")
    public ResponseEntity<ApiResponseDto<LearningPathProgressDetailResponseDto>> update(
            @PathVariable Long learningPathId,
            @AuthenticationPrincipal User user,
            @Validated @RequestBody LearningPathProgressRequestDto dto) {
        LearningPathProgressKey key = new LearningPathProgressKey(learningPathId, user.getId());
        return ResponseEntity.ok(
                ApiResponseDto.ok("Cập nhật tiến trình learning path thành công",
                        learningPathProgressService.update(key, dto))
        );
    }

    @DeleteMapping("/{learningPathId}")
    public ResponseEntity<ApiResponseDto<Void>> delete(
            @PathVariable Long learningPathId,
            @AuthenticationPrincipal User user) {
        LearningPathProgressKey key = new LearningPathProgressKey(learningPathId, user.getId());
        learningPathProgressService.delete(key);
        return ResponseEntity.ok(
                ApiResponseDto.ok("Xóa tiến trình learning path thành công", null)
        );
    }
}
