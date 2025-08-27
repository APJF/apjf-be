package fu.sep.apjf.controller;

import fu.sep.apjf.dto.request.ChapterRequestDto;
import fu.sep.apjf.dto.response.*;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.service.ChapterService;
import fu.sep.apjf.service.ExamService;
import fu.sep.apjf.service.UnitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/chapters")
@RequiredArgsConstructor
public class ChapterController {

    private final ChapterService chapterService;
    private final UnitService unitService;
    private final ExamService examService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<ChapterResponseDto>>> getAll() {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Danh sách chương", chapterService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<ChapterProgressResponseDto>> getById(@AuthenticationPrincipal User user, @PathVariable String id) {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Chi tiết chương", chapterService.findById(id,user.getId())));
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<ApiResponseDto<ChapterDetailWithProgressResponseDto>> getChapterDetail(
            @AuthenticationPrincipal User user,
            @PathVariable String id) {

        return ResponseEntity.ok(
                ApiResponseDto.ok("Chi tiết chương học", chapterService.getChapterDetailWithProgress(user, id))
        );
    }

    @GetMapping("/{chapterId}/exams")
    public ResponseEntity<ApiResponseDto<List<ExamListResponseDto>>> getExamsByChapterId(@PathVariable String chapterId) {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Danh sách exams của chương", examService.findByChapterId(chapterId)));
    }

    @GetMapping("/{chapterId}/units")
    public ResponseEntity<ApiResponseDto<List<UnitResponseDto>>> getChapterUnits(
            @PathVariable String chapterId) {

        List<UnitResponseDto> units = unitService.findByChapterId(chapterId);
        return ResponseEntity.ok(ApiResponseDto.ok("Danh sách units của chapter", units));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto<ChapterResponseDto>> create(
            @Valid @RequestBody ChapterRequestDto dto,
            @AuthenticationPrincipal User user) {
        ChapterResponseDto created = chapterService.create(dto, user.getId());
        return ResponseEntity.created(URI.create("/api/chapters/" + created.id()))
                .body(ApiResponseDto.ok("Tạo chương thành công", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<ChapterResponseDto>> update(
            @PathVariable String id,
            @Valid @RequestBody ChapterRequestDto dto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Cập nhật chương thành công", chapterService.update(id, dto, user.getId())));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponseDto<ChapterResponseDto>> deactivate(
            @PathVariable String id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Vô hiệu hóa chương thành công", chapterService.deactivate(id, user.getId())));
    }

}