package fu.sep.cms.controller;

import fu.sep.cms.entity.Chapter;
import fu.sep.cms.service.ChapterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chapters")
@RequiredArgsConstructor
public class ChapterController {

    private final ChapterService chapterService;

    // 1. Tạo mới
    @PostMapping
    public ResponseEntity<Chapter> create(@RequestBody Chapter chapter) {
        Chapter saved = chapterService.createChapter(chapter);
        return ResponseEntity.status(201).body(saved);
    }

    // 2. Cập nhật
    @PutMapping("/{id}")
    public ResponseEntity<Chapter> update(
            @PathVariable Long id,
            @RequestBody Chapter chapter
    ) {
        Chapter updated = chapterService.updateChapter(id, chapter);
        return ResponseEntity.ok(updated);
    }

    // 3. Xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        chapterService.deleteChapter(id);
        return ResponseEntity.noContent().build();
    }

    // 4. Xem chi tiết
    @GetMapping("/{id}")
    public ResponseEntity<Chapter> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(chapterService.getChapterById(id));
    }

    // 5. List with filter/sort/pagination
    @GetMapping
    public ResponseEntity<Page<Chapter>> list(
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderNumber") String sortBy,
            @RequestParam(defaultValue = "asc") String dir
    ) {
        Sort sort = "asc".equalsIgnoreCase(dir)
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Chapter> result = chapterService.getChapters(subjectId, keyword, pageable);
        return ResponseEntity.ok(result);
    }
}
