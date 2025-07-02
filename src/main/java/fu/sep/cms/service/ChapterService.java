package fu.sep.cms.service;

import fu.sep.cms.dto.ChapterDto;
import fu.sep.cms.dto.UnitDto;
import fu.sep.cms.entity.Chapter;
import fu.sep.cms.entity.Course;
import fu.sep.cms.entity.Status;
import fu.sep.cms.repository.ChapterRepository;
import fu.sep.cms.repository.CourseRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChapterService {

    private final ChapterRepository chapterRepo;
    private final CourseRepository courseRepo;

    /* ---------- READ ---------- */

    @Transactional(readOnly = true)
    public List<ChapterDto> findAll() {
        return chapterRepo.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ChapterDto findById(String id) {
        return toDto(chapterRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Chapter not found")));
    }

    /* ---------- CREATE ---------- */
    public ChapterDto create(@Valid ChapterDto dto) {
        Course parent = courseRepo.findById(dto.courseId())
                .orElseThrow(() -> new EntityNotFoundException("Course missing"));

        if (chapterRepo.existsById(dto.id()))
            throw new IllegalArgumentException("Chapter id already exists");

        Chapter ch = Chapter.builder()
                .id(dto.id())
                .title(dto.title())
                .description(dto.description())
                .status(Status.DRAFT)
                .course(parent)
                .build();

        // Set prerequisite chapter if provided
        if (dto.prerequisiteChapterId() != null) {
            Chapter prerequisite = chapterRepo.findById(dto.prerequisiteChapterId())
                .orElseThrow(() -> new EntityNotFoundException("Prerequisite chapter not found"));
            ch.setPrerequisiteChapter(prerequisite);
        }

        return toDto(chapterRepo.save(ch));
    }

    /* ---------- UPDATE ---------- */
    public ChapterDto update(String currentId, @Valid ChapterDto dto) {
        Chapter ch = chapterRepo.findById(currentId)
                .orElseThrow(() -> new EntityNotFoundException("Chapter not found"));

        ch.setTitle(dto.title());
        ch.setDescription(dto.description());
        ch.setStatus(Status.DRAFT);

        // Update prerequisite chapter
        if (dto.prerequisiteChapterId() != null) {
            Chapter prerequisite = chapterRepo.findById(dto.prerequisiteChapterId())
                .orElseThrow(() -> new EntityNotFoundException("Prerequisite chapter not found"));
            ch.setPrerequisiteChapter(prerequisite);
        } else {
            ch.setPrerequisiteChapter(null);
        }

        if (!dto.id().equals(currentId)) {
            if (chapterRepo.existsById(dto.id()))
                throw new IllegalArgumentException("New chapter id already exists");
            chapterRepo.delete(ch);
            ch.setId(dto.id());
        }
        return toDto(chapterRepo.save(ch));
    }

    /* ---------- helper ---------- */

    private ChapterDto toDto(Chapter ch) {
        Set<UnitDto> units = ch.getUnits().stream()
                .map(u -> new UnitDto(u.getId(), u.getTitle(),
                        u.getDescription(), u.getStatus(), ch.getId(),
                        u.getPrerequisiteUnit() != null ? u.getPrerequisiteUnit().getId() : null))
                .collect(Collectors.toSet());

        return new ChapterDto(ch.getId(), ch.getTitle(), ch.getDescription(),
                ch.getStatus(), ch.getCourse().getId(),
                ch.getPrerequisiteChapter() != null ? ch.getPrerequisiteChapter().getId() : null,
                units);
    }
}