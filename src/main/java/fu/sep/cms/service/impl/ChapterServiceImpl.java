package fu.sep.cms.service.impl;

import fu.sep.cms.entity.Chapter;
import fu.sep.cms.repository.ChapterRepository;
import fu.sep.cms.service.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChapterServiceImpl implements ChapterService {

    private final ChapterRepository chapterRepository;

    @Autowired
    public ChapterServiceImpl(ChapterRepository chapterRepository) {
        this.chapterRepository = chapterRepository;
    }

    @Override
    public Chapter addChapter(Chapter chapter) {
        return chapterRepository.save(chapter);
    }

    @Override
    public Chapter updateChapter(Long id, Chapter updatedChapter) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chapter not found"));
        chapter.setTitle(updatedChapter.getTitle());
        chapter.setContent(updatedChapter.getContent());
        chapter.setSubject(updatedChapter.getSubject());
        return chapterRepository.save(chapter);
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
    public Page<Chapter> getAllChapters(int page, int size) {
        return chapterRepository.findAll(PageRequest.of(page, size));
    }

    @Override
    public Page<Chapter> searchChapters(String keyword, int page, int size) {
        return chapterRepository.findByTitleContainingIgnoreCase(keyword, PageRequest.of(page, size));
    }
}
