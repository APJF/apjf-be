package fu.sep.apjf.service;

import fu.sep.apjf.dto.ChapterDto;
import fu.sep.apjf.dto.CourseDetailDto;
import fu.sep.apjf.dto.CourseDto;
import fu.sep.apjf.dto.TopicDto;
import fu.sep.apjf.dto.UnitDto;
import fu.sep.apjf.entity.ApprovalRequest;
import fu.sep.apjf.entity.Chapter;
import fu.sep.apjf.entity.Course;
import fu.sep.apjf.entity.EnumClass;
import fu.sep.apjf.repository.CourseRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
    public Page<CourseDto> findAll(int page, int size) {
        return courseRepo.findAll(PageRequest.of(page, size))
                .map(this::toDto);
    }

    // Thêm các phương thức mới tận dụng repository đã cập nhật
    @Transactional(readOnly = true)
    public List<CourseDto> findByStatus(EnumClass.Status status) {
        return courseRepo.findByStatus(status).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<CourseDto> findByStatus(EnumClass.Status status, int page, int size) {
        return courseRepo.findByStatus(status, PageRequest.of(page, size))
                .map(this::toDto);
    }

    @Transactional(readOnly = true)
    public List<CourseDto> findByLevel(EnumClass.Level level) {
        return courseRepo.findByLevel(level).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<CourseDto> findByLevel(EnumClass.Level level, int page, int size) {
        return courseRepo.findByLevel(level, PageRequest.of(page, size))
                .map(this::toDto);
    }

    @Transactional(readOnly = true)
    public List<CourseDto> findEntryLevelCourses() {
        return courseRepo.findByPrerequisiteCourseIsNull().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CourseDto> searchByTitle(String title) {
        return courseRepo.findByTitleContainingIgnoreCase(title).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<CourseDto> searchByTitle(String title, int page, int size) {
        return courseRepo.findByTitleContainingIgnoreCase(title, PageRequest.of(page, size))
                .map(this::toDto);
    }

    @Transactional(readOnly = true)
    public List<CourseDto> findPublishedCoursesByLevel(EnumClass.Level level) {
        return courseRepo.findByStatusAndLevel(EnumClass.Status.PUBLISHED, level).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public long countByStatus(EnumClass.Status status) {
        return courseRepo.countByStatus(status);
    }

    @Transactional(readOnly = true)
    public long countByLevel(EnumClass.Level level) {
        return courseRepo.countByLevel(level);
    }

    @Transactional(readOnly = true)
    public boolean existsByTitle(String title) {
        return courseRepo.existsByTitleIgnoreCase(title);
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
        log.info("Nhân viên {} tạo khóa học m���i với mã: {}", staffId, dto.id());

        // Validate prerequisite course exists
        if (dto.prerequisiteCourseId() != null) {
            courseRepo.findById(dto.prerequisiteCourseId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khóa học tiên quyết"));
        }

        if (courseRepo.existsById(dto.id()))
            throw new IllegalArgumentException("Mã khóa học đã tồn tại");

        Course course = Course.builder()
                .id(dto.id())
                .title(dto.title())
                .description(dto.description())
                .duration(dto.duration())
                .level(dto.level())
                .prerequisiteCourse(dto.prerequisiteCourseId() != null ?
                        courseRepo.findById(dto.prerequisiteCourseId()).orElse(null) : null)
                .status(EnumClass.Status.DRAFT) // Set as DRAFT until approved
                .build();

        Course savedCourse = courseRepo.save(course);

        // Auto-create approval request for this new course
        approvalRequestService.autoCreateApprovalRequest(
                ApprovalRequest.TargetType.COURSE,
                savedCourse.getId(),
                ApprovalRequest.RequestType.CREATE,
                staffId
        );

        log.info("Tạo khóa học {} và yêu cầu phê duyệt thành công", savedCourse.getId());
        return toDto(savedCourse);
    }

    /* ---------- UPDATE ---------- */
    public CourseDto update(String currentId, @Valid CourseDto dto, String staffId) {
        log.info("Nhân viên {} cập nhật khóa học với mã: {}", staffId, currentId);

        Course course = courseRepo.findById(currentId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khóa học"));

        // Validate prerequisite course exists
        if (dto.prerequisiteCourseId() != null) {
            courseRepo.findById(dto.prerequisiteCourseId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khóa học tiên quyết"));
        }

        course.setTitle(dto.title());
        course.setDescription(dto.description());
        course.setDuration(dto.duration());
        course.setLevel(dto.level());
        course.setStatus(EnumClass.Status.DRAFT); // Reset to DRAFT when updated

        // Update prerequisite course
        if (dto.prerequisiteCourseId() != null) {
            Course prerequisite = courseRepo.findById(dto.prerequisiteCourseId()).orElse(null);
            course.setPrerequisiteCourse(prerequisite);
        } else {
            course.setPrerequisiteCourse(null);
        }

        /* Đổi PK nếu khác */
        if (!dto.id().equals(currentId)) {
            if (courseRepo.existsById(dto.id()))
                throw new IllegalArgumentException("Mã khóa học mới đã tồn tại");
            courseRepo.delete(course);
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

        log.info("Cập nhật khóa học {} và tạo yêu cầu phê duyệt thành công", savedCourse.getId());
        return toDto(savedCourse);
    }

    @Transactional(readOnly = true)
    public Page<CourseDto> findAll(int page, int size, String sortBy, String direction,
                                  String title, EnumClass.Level level, EnumClass.Status status) {
        // Tạo Sort object
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(sortDirection, sortBy);

        // Tạo PageRequest
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        // Tạo Specification cho filtering
        Specification<Course> spec = (root, query, cb) -> cb.conjunction();

        if (title != null && !title.trim().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
        }

        if (level != null) {
            spec = spec.and((root, query, cb) ->
                cb.equal(root.get("level"), level));
        }

        if (status != null) {
            spec = spec.and((root, query, cb) ->
                cb.equal(root.get("status"), status));
        }

        return courseRepo.findAll(spec, pageRequest).map(this::toDto);
    }

    /* ---------- Mapping helpers ---------- */

    private CourseDto toDto(Course c) {
        Set<TopicDto> topicDtos = c.getTopics().stream()
                .map(topic -> new TopicDto(topic.getId(), topic.getName()))
                .collect(Collectors.toSet());

        return new CourseDto(c.getId(), c.getTitle(), c.getDescription(),
                c.getDuration(), c.getLevel(),
                c.getImage(), c.getRequirement(), c.getStatus(),
                c.getPrerequisiteCourse() != null ? c.getPrerequisiteCourse().getId() : null,
                topicDtos);
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
}