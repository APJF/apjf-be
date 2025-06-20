package fu.sep.cms.service.impl;

import fu.sep.cms.entity.Chapter;
import fu.sep.cms.repository.ChapterRepository;
import fu.sep.cms.service.ChapterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChapterServiceImpl implements ChapterService {

    private final ChapterRepository chapterRepository;

    @Override
    public Chapter createChapter(Chapter chapter) {
        chapter.setCreatedAt(java.time.LocalDateTime.now());
        chapter.setUpdatedAt(java.time.LocalDateTime.now());
        return chapterRepository.save(chapter);
    }

    @Override
    public Chapter updateChapter(Long id, Chapter incoming) {
        Chapter ch = chapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chapter not found"));
        ch.setTitle(incoming.getTitle());
        ch.setDescription(incoming.getDescription());
        ch.setOrderNumber(incoming.getOrderNumber());
        ch.setSubject(incoming.getSubject());
        ch.setUpdatedAt(java.time.LocalDateTime.now());
        return chapterRepository.save(ch);
    }

    @Override
    public void deleteChapter(Long id) {
        chapterRepository.deleteById(id);
    }

    @Override
    public Chapter getChapterById(Long id) {
        return chapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chapter not found"));
    }

    @Override
    public Page<Chapter> getChapters(Long subjectId, String keyword, Pageable pageable) {
        boolean hasSubject = subjectId != null;
        boolean hasKeyword = keyword != null && !keyword.isBlank();

        if (hasSubject && hasKeyword) {
            return chapterRepository.findBySubjectIdAndTitleContainingIgnoreCase(subjectId, keyword, pageable);
        } else if (hasSubject) {
            return chapterRepository.findBySubjectId(subjectId, pageable);
        } else if (hasKeyword) {
            return chapterRepository.findByTitleContainingIgnoreCase(keyword, pageable);
        } else {
            return chapterRepository.findAll(pageable);
        }
    }
}
