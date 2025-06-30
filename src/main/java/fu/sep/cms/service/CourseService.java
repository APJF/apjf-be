package fu.sep.cms.service;

import fu.sep.cms.dto.ChapterDto;
import fu.sep.cms.dto.CourseDetailDto;
import fu.sep.cms.dto.CourseDto;
import fu.sep.cms.dto.UnitDto;
import fu.sep.cms.entity.Chapter;
import fu.sep.cms.entity.Course;
import fu.sep.cms.entity.Course.Level;
import fu.sep.cms.entity.Status;
import fu.sep.cms.repository.CourseRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {

    private final CourseRepository courseRepo;

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
    public CourseDto create(@Valid CourseDto dto) {
        if (courseRepo.existsById(dto.id()))
            throw new IllegalArgumentException("Course id already exists");

        Course entity = toEntity(dto);
        entity.setId(dto.id());
        entity.setStatus(Status.DRAFT);
        return toDto(courseRepo.save(entity));
    }

    /* ---------- UPDATE ---------- */
    public CourseDto update(String currentId, @Valid CourseDto dto) {
        Course course = courseRepo.findById(currentId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));

        course.setTitle(dto.title());
        course.setDescription(dto.description());
        course.setEstimatedDuration(dto.estimatedDuration());
        course.setLevel(dto.level());
        course.setImage(dto.image());
        course.setRequirement(dto.requirement());
        course.setStatus(Status.DRAFT);

        /* Đổi PK nếu khác */
        if (!dto.id().equals(currentId)) {
            if (courseRepo.existsById(dto.id()))
                throw new IllegalArgumentException("New course id already exists");
            courseRepo.delete(course);      // xóa hàng PK cũ
            course.setId(dto.id());
        }
        return toDto(courseRepo.save(course));
    }

    /* ---------- Mapping helpers ---------- */

    private CourseDto toDto(Course c) {
        return new CourseDto(c.getId(), c.getTitle(), c.getDescription(),
                c.getEstimatedDuration(), c.getLevel(),
                c.getImage(), c.getRequirement(), c.getStatus());
    }

    private ChapterDto toChapterDto(Chapter ch) {
        Set<UnitDto> units = ch.getUnits().stream()
                .map(u -> new UnitDto(u.getId(), u.getTitle(),
                        u.getDescription(), u.getStatus(), ch.getId()))
                .collect(Collectors.toSet());

        return new ChapterDto(ch.getId(), ch.getTitle(), ch.getDescription(),
                ch.getStatus(), ch.getCourse().getId(), units);
    }

    private Course toEntity(CourseDto dto) {
        return Course.builder()
                .title(dto.title())
                .description(dto.description())
                .estimatedDuration(dto.estimatedDuration())
                .level(Optional.ofNullable(dto.level()).orElse(Level.BEGINNER))
                .image(dto.image())
                .requirement(dto.requirement())
                .status(Optional.ofNullable(dto.status()).orElse(Status.DRAFT))
                .build();
    }
}