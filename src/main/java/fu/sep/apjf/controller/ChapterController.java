package fu.sep.apjf.controller;

import fu.sep.apjf.dto.ApiResponse;
import fu.sep.apjf.dto.ChapterDto;
import fu.sep.apjf.service.ChapterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/chapters")
@RequiredArgsConstructor
public class ChapterController {

    private final ChapterService chapterService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ChapterDto>>> getAll() {
        return ResponseEntity.ok(
                ApiResponse.ok("Danh sách chương", chapterService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ChapterDto>> getById(@PathVariable String id) {
        return ResponseEntity.ok(
                ApiResponse.ok("Chi tiết chương", chapterService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ChapterDto>> create(@Valid @RequestBody ChapterDto dto,
                                                          @RequestHeader("X-User-Id") String staffId) {
        ChapterDto created = chapterService.create(dto, staffId);
        return ResponseEntity.created(URI.create("/api/chapters/" + created.id()))
                .body(ApiResponse.ok("Tạo chương thành công", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ChapterDto>> update(@PathVariable String id,
                                                          @Valid @RequestBody ChapterDto dto,
                                                          @RequestHeader("X-User-Id") String staffId) {
        return ResponseEntity.ok(
                ApiResponse.ok("Cập nhật chương thành công", chapterService.update(id, dto, staffId)));
    }
}