package fu.sep.apjf.controller;

import fu.sep.apjf.dto.request.ChapterProgressRequestDto;
import fu.sep.apjf.dto.response.ChapterProgressResponseDto;
import fu.sep.apjf.entity.ChapterProgressKey;
import fu.sep.apjf.service.ChapterProgressService;
import fu.sep.apjf.dto.response.ApiResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import fu.sep.apjf.entity.User;

import java.util.List;

@RestController
@RequestMapping("/api/chapter-progress")
@RequiredArgsConstructor
@Slf4j
public class ChapterProgressController {

    private final ChapterProgressService chapterProgressService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponseDto<List<ChapterProgressResponseDto>>> getByUserId(
            @PathVariable Long userId) {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Danh sách tiến trình chương học của user",
                        chapterProgressService.findByUserId(userId))
        );
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponseDto<List<ChapterProgressResponseDto>>> getByUserAndCourse(
            @PathVariable String courseId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Danh sách tiến trình chương học trong khóa",
                        chapterProgressService.getByUserIdAndCourseId(user.getId(), courseId))
        );
    }

    @GetMapping("/{chapterId}/detail")
    public ResponseEntity<ApiResponseDto<ChapterProgressResponseDto>> getDetail(
            @PathVariable String chapterId,
            @AuthenticationPrincipal User user) {
        ChapterProgressKey key = new ChapterProgressKey(chapterId, user.getId());
        return ResponseEntity.ok(
                ApiResponseDto.ok("Chi tiết tiến trình chương học",
                        chapterProgressService.findById(key))
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto<ChapterProgressResponseDto>> create(
            @Validated @RequestBody ChapterProgressRequestDto dto) {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Tạo tiến trình chương học thành công",
                        chapterProgressService.create(dto))
        );
    }

    @PutMapping("/{chapterId}")
    public ResponseEntity<ApiResponseDto<ChapterProgressResponseDto>> update(
            @PathVariable String chapterId,
            @AuthenticationPrincipal User user,
            @Validated @RequestBody ChapterProgressRequestDto dto) {
        ChapterProgressKey key = new ChapterProgressKey(chapterId, user.getId());
        return ResponseEntity.ok(
                ApiResponseDto.ok("Cập nhật tiến trình chương học thành công",
                        chapterProgressService.update(key, dto))
        );
    }

    @DeleteMapping("/{chapterId}")
    public ResponseEntity<ApiResponseDto<Void>> delete(
            @PathVariable String chapterId,
            @AuthenticationPrincipal User user) {
        ChapterProgressKey key = new ChapterProgressKey(chapterId, user.getId());
        chapterProgressService.delete(key);
        return ResponseEntity.ok(
                ApiResponseDto.ok("Xóa tiến trình chương học thành công", null)
        );
    }
}
