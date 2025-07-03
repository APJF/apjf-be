package fu.sep.cms.service;

import fu.sep.cms.dto.ChapterDto;
import fu.sep.cms.dto.UnitDto;
import fu.sep.cms.entity.ApprovalRequest;
import fu.sep.cms.entity.Chapter;
import fu.sep.cms.entity.Course;
import fu.sep.cms.entity.Status;
import fu.sep.cms.repository.ChapterRepository;
import fu.sep.cms.repository.CourseRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChapterService {

    private final ChapterRepository chapterRepo;
    private final CourseRepository courseRepo;
    private final ApprovalRequestService approvalRequestService;

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
    public ChapterDto create(@Valid ChapterDto dto, String staffId) {
        log.info("Staff {} creating new chapter with ID: {}", staffId, dto.id());

        Course parent = courseRepo.findById(dto.courseId())
                .orElseThrow(() -> new EntityNotFoundException("Course missing"));

        if (chapterRepo.existsById(dto.id()))
            throw new IllegalArgumentException("Chapter id already exists");

        Chapter ch = Chapter.builder()
                .id(dto.id())
                .title(dto.title())
                .description(dto.description())
                .status(Status.DRAFT) // Set as DRAFT until approved
                .course(parent)
                .build();

        // Set prerequisite chapter if provided
        if (dto.prerequisiteChapterId() != null) {
            Chapter prerequisite = chapterRepo.findById(dto.prerequisiteChapterId())
                .orElseThrow(() -> new EntityNotFoundException("Prerequisite chapter not found"));
            ch.setPrerequisiteChapter(prerequisite);
        }

        Chapter savedChapter = chapterRepo.save(ch);

        // Auto-create approval request for this new chapter
        approvalRequestService.autoCreateApprovalRequest(
                ApprovalRequest.TargetType.CHAPTER,
                savedChapter.getId(),
                ApprovalRequest.RequestType.CREATE,
                staffId
        );

        log.info("Successfully created chapter {} and approval request", savedChapter.getId());
        return toDto(savedChapter);
    }

    /* ---------- UPDATE ---------- */
    public ChapterDto update(String currentId, @Valid ChapterDto dto, String staffId) {
        log.info("Staff {} updating chapter with ID: {}", staffId, currentId);

        Chapter chapter = chapterRepo.findById(currentId)
                .orElseThrow(() -> new EntityNotFoundException("Chapter not found"));

        chapter.setTitle(dto.title());
        chapter.setDescription(dto.description());
        chapter.setStatus(Status.DRAFT); // Reset to DRAFT when updated

        // Update prerequisite chapter
        if (dto.prerequisiteChapterId() != null) {
            Chapter prerequisite = chapterRepo.findById(dto.prerequisiteChapterId())
                .orElseThrow(() -> new EntityNotFoundException("Prerequisite chapter not found"));
            chapter.setPrerequisiteChapter(prerequisite);
        } else {
            chapter.setPrerequisiteChapter(null);
        }

        /* Đổi PK nếu khác */
        if (!dto.id().equals(currentId)) {
            if (chapterRepo.existsById(dto.id()))
                throw new IllegalArgumentException("New chapter id already exists");
            chapterRepo.delete(chapter);
            chapter.setId(dto.id());
        }

        Chapter savedChapter = chapterRepo.save(chapter);

        // Auto-create approval request for this chapter update
        approvalRequestService.autoCreateApprovalRequest(
                ApprovalRequest.TargetType.CHAPTER,
                savedChapter.getId(),
                ApprovalRequest.RequestType.UPDATE,
                staffId
        );

        log.info("Successfully updated chapter {} and created approval request", savedChapter.getId());
        return toDto(savedChapter);
    }

    /* ---------- Mapping helpers ---------- */

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