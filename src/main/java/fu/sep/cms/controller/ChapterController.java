package fu.sep.cms.controller;

import fu.sep.cms.entity.Chapter;
import fu.sep.cms.service.ChapterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chapters")
@RequiredArgsConstructor
public class ChapterController {

    private final ChapterService chapterService;

    @PostMapping
    public Chapter create(@RequestBody Chapter chapter) {
        return chapterService.addChapter(chapter);
    }

    @PutMapping("/{id}")
    public Chapter update(@PathVariable Long id, @RequestBody Chapter chapter) {
        return chapterService.updateChapter(id, chapter);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        chapterService.deleteChapter(id);
    }

    @GetMapping("/{id}")
    public Chapter getById(@PathVariable Long id) {
        return chapterService.getChapterById(id);
    }

    @GetMapping
    public Page<Chapter> getAll(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size) {
        return chapterService.getAllChapters(page, size);
    }

    @GetMapping("/search")
    public Page<Chapter> search(@RequestParam String keyword,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size) {
        return chapterService.searchChapters(keyword, page, size);
    }
}
