package fu.sep.cms.service;

import fu.sep.cms.dto.ChapterDto;
import fu.sep.cms.dto.CourseDetailDto;
import fu.sep.cms.dto.CourseDto;
import fu.sep.cms.dto.UnitDto;
import fu.sep.cms.entity.ApprovalRequest;
import fu.sep.cms.entity.Chapter;
import fu.sep.cms.entity.Course;
import fu.sep.cms.entity.Course.Level;
import fu.sep.cms.entity.EnumClass;
import fu.sep.cms.repository.CourseRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CourseService {

    private final CourseRepository courseRepo;
    private final ApprovalRequestService approvalRequestService;

    /* ---------- READ ---------- */

    @Transactional(readOnly = true)
    public List<CourseDto> findAll() {
        return courseRepo.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CourseDto findById(String id) {
        return toDto(courseRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found")));
    }

    @Transactional(readOnly = true)
    public CourseDetailDto findDetail(String id) {
        Course course = courseRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));

        Set<ChapterDto> chapters = course.getChapters().stream()
                .map(this::toChapterDto)
                .collect(Collectors.toSet());

        return new CourseDetailDto(toDto(course), chapters);
    }

    /* ---------- CREATE ---------- */
    public CourseDto create(@Valid CourseDto dto, String staffId) {
        log.info("Staff {} creating new course with ID: {}", staffId, dto.id());

        if (courseRepo.existsById(dto.id()))
            throw new IllegalArgumentException("Course id already exists");

        Course entity = toEntity(dto);
        entity.setId(dto.id());
        entity.setStatus(EnumClass.Status.DRAFT); // Set as DRAFT until approved

        // Set prerequisite course if provided
        if (dto.prerequisiteCourseId() != null) {
            Course prerequisite = courseRepo.findById(dto.prerequisiteCourseId())
                .orElseThrow(() -> new EntityNotFoundException("Prerequisite course not found"));
            entity.setPrerequisiteCourse(prerequisite);
        }

        Course savedCourse = courseRepo.save(entity);

        // Auto-create approval request for this new course
        approvalRequestService.autoCreateApprovalRequest(
                ApprovalRequest.TargetType.COURSE,
                savedCourse.getId(),
                ApprovalRequest.RequestType.CREATE,
                staffId
        );

        log.info("Successfully created course {} and approval request", savedCourse.getId());
        return toDto(savedCourse);
    }

    /* ---------- UPDATE ---------- */
    public CourseDto update(String currentId, @Valid CourseDto dto, String staffId) {
        log.info("Staff {} updating course with ID: {}", staffId, currentId);

        Course course = courseRepo.findById(currentId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));

        course.setTitle(dto.title());
        course.setDescription(dto.description());
        course.setDuration(dto.duration());
        course.setLevel(dto.level());
        course.setImage(dto.image());
        course.setRequirement(dto.requirement());
        course.setStatus(EnumClass.Status.DRAFT); // Reset to DRAFT when updated

        // Update prerequisite course
        if (dto.prerequisiteCourseId() != null) {
            Course prerequisite = courseRepo.findById(dto.prerequisiteCourseId())
                .orElseThrow(() -> new EntityNotFoundException("Prerequisite course not found"));
            course.setPrerequisiteCourse(prerequisite);
        } else {
            course.setPrerequisiteCourse(null);
        }

        /* Đổi PK nếu khác */
        if (!dto.id().equals(currentId)) {
            if (courseRepo.existsById(dto.id()))
                throw new IllegalArgumentException("New course id already exists");
            courseRepo.delete(course);      // xóa hàng PK cũ
            course.setId(dto.id());
        }

        Course savedCourse = courseRepo.save(course);

        // Auto-create approval request for this course update
        approvalRequestService.autoCreateApprovalRequest(
                ApprovalRequest.TargetType.COURSE,
                savedCourse.getId(),
                ApprovalRequest.RequestType.UPDATE,
                staffId
        );

        log.info("Successfully updated course {} and created approval request", savedCourse.getId());
        return toDto(savedCourse);
    }

    /* ---------- Mapping helpers ---------- */

    private CourseDto toDto(Course c) {
        return new CourseDto(c.getId(), c.getTitle(), c.getDescription(),
                c.getDuration(), c.getLevel(),
                c.getImage(), c.getRequirement(), c.getStatus(),
                c.getPrerequisiteCourse() != null ? c.getPrerequisiteCourse().getId() : null);
    }

    private ChapterDto toChapterDto(Chapter ch) {
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

    private Course toEntity(CourseDto dto) {
        return Course.builder()
                .title(dto.title())
                .description(dto.description())
                .duration(dto.duration())
                .level(Optional.ofNullable(dto.level()).orElse(Level.BEGINNER))
                .image(dto.image())
                .requirement(dto.requirement())
                .status(Optional.ofNullable(dto.status()).orElse(EnumClass.Status.DRAFT))
                .build();
    }
}