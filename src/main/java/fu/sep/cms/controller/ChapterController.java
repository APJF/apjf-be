package fu.sep.cms.controller;

import fu.sep.cms.dto.ApiResponse;
import fu.sep.cms.dto.ChapterDto;
import fu.sep.cms.service.ChapterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/chapters")
//@RequiredArgsConstructor
public class ChapterController {

    private final ChapterService chapterService;
    public ChapterController(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ChapterDto>>> getAll() {
        return ResponseEntity.ok(
                ApiResponse.ok("List chapters", chapterService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ChapterDto>> getById(@PathVariable String id) {
        return ResponseEntity.ok(
                ApiResponse.ok("Chapter", chapterService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ChapterDto>> create(@RequestBody ChapterDto dto) {
        ChapterDto created = chapterService.create(dto);
        return ResponseEntity.created(URI.create("/api/chapters/" + created.id()))
                .body(ApiResponse.ok("Chapter created", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ChapterDto>> update(@PathVariable String id,
                                                          @RequestBody ChapterDto dto) {
        return ResponseEntity.ok(
                ApiResponse.ok("Chapter updated", chapterService.update(id, dto)));
    }
}