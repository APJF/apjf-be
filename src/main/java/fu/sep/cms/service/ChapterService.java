package fu.sep.cms.service;

import fu.sep.cms.entity.Chapter;
import org.springframework.data.domain.Page;

public interface ChapterService {
    Chapter addChapter(Chapter chapter);
    Chapter updateChapter(Long id, Chapter updatedChapter);
    void deleteChapter(Long id);
    Chapter getChapterById(Long id);
    Page<Chapter> getAllChapters(int page, int size);
    Page<Chapter> searchChapters(String keyword, int page, int size);
}
