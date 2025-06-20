package fu.sep.cms.service;

import fu.sep.cms.entity.Chapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChapterService {
    Chapter createChapter(Chapter chapter);
    Chapter updateChapter(Long id, Chapter chapter);
    void deleteChapter(Long id);
    Chapter getChapterById(Long id);

    /**
     * Lấy danh sách chapter với tùy chọn filter và phân trang/sort.
     * @param subjectId lọc theo subjectId (null để bỏ qua)
     * @param keyword   tìm trong title (null để bỏ qua)
     * @param pageable  phân trang & sort
     */
    Page<Chapter> getChapters(Long subjectId, String keyword, Pageable pageable);
}
