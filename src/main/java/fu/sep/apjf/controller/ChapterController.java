package fu.sep.apjf.controller;

import fu.sep.apjf.dto.request.ChapterRequestDto;
import fu.sep.apjf.dto.response.ChapterResponseDto;
import fu.sep.apjf.dto.response.ApiResponseDto;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.service.ChapterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/chapters")
@RequiredArgsConstructor
@Slf4j
public class ChapterController {

    private final ChapterService chapterService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<ChapterResponseDto>>> getAll() {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Danh sách chương", chapterService.findAll()));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponseDto<List<ChapterResponseDto>>> getAllByCourseId(@PathVariable String courseId) {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Danh sách chương theo khóa học", chapterService.findByCourseId(courseId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<ChapterResponseDto>> getById(@PathVariable String id) {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Chi tiết chương", chapterService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto<ChapterResponseDto>> create(
            @Valid @RequestBody ChapterRequestDto dto,
            @AuthenticationPrincipal User user) {

        log.info("Staff {} đang tạo chương mới: {}", user.getUsername(), dto.id());

        ChapterResponseDto created = chapterService.create(dto, user.getId());
        return ResponseEntity.created(URI.create("/api/chapters/" + created.id()))
                .body(ApiResponseDto.ok("Tạo chương thành công", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<ChapterResponseDto>> update(
            @PathVariable String id,
            @Valid @RequestBody ChapterRequestDto dto,
            @AuthenticationPrincipal User user) {

        log.info("Staff {} đang cập nhật chương: {}", user.getUsername(), id);

        return ResponseEntity.ok(
                ApiResponseDto.ok("Cập nhật chương thành công", chapterService.update(id, dto, user.getId())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> delete(
            @PathVariable String id,
            @AuthenticationPrincipal User user) {

        log.info("Staff {} đang xóa chương: {}", user.getUsername(), id);

        chapterService.delete(id);
        return ResponseEntity.ok(ApiResponseDto.ok("Xóa chương thành công", null));
    }
}