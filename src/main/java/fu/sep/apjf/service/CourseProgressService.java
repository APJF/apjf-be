package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.CourseProgressRequestDto;
import fu.sep.apjf.dto.response.ChapterProgressResponseDto;
import fu.sep.apjf.dto.response.CourseProgressResponseDto;
import fu.sep.apjf.dto.response.UnitProgressResponseDto;
import fu.sep.apjf.entity.Course;
import fu.sep.apjf.entity.CourseProgress;
import fu.sep.apjf.entity.CourseProgressKey;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.mapper.CourseProgressMapper;
import fu.sep.apjf.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CourseProgressService {

    private final CourseProgressRepository courseProgressRepo;
    private final CourseRepository courseRepo;
    private final UserRepository userRepo;
    private final CourseProgressMapper courseProgressMapper;
    private final ChapterProgressRepository chapterProgressRepo;
    private final UnitProgressRepository unitProgressRepo;

    @Transactional(readOnly = true)
    public List<CourseProgressResponseDto> findByUserId(Long userId) {
        return courseProgressRepo.findByUserId(userId)
                .stream()
                .map(courseProgressMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean isCourseCompleted(String courseId, Long userId) {
        return courseProgressRepo.existsByCourseIdAndUserIdAndCompletedTrue(courseId, userId);
    }

    @Transactional(readOnly = true)
    public CourseProgressResponseDto findById(CourseProgressKey id) {
        return courseProgressMapper.toResponseDto(
                courseProgressRepo.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tiến trình khóa học"))
        );
    }

    public CourseProgressResponseDto create(@Valid CourseProgressRequestDto dto) {
        log.info("Tạo tiến trình khóa học cho user {} và course {}", dto.userId(), dto.courseId());

        Course course = courseRepo.findById(dto.courseId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khóa học"));

        User user = userRepo.findById(dto.userId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng"));

        CourseProgress progress = courseProgressMapper.toEntity(dto, course, user);
        CourseProgress saved = courseProgressRepo.save(progress);

        return courseProgressMapper.toResponseDto(saved);
    }

    public CourseProgressResponseDto update(CourseProgressKey id, @Valid CourseProgressRequestDto dto) {
        log.info("Cập nhật tiến trình khóa học {} cho user {}", id.getCourseId(), id.getUserId());

        CourseProgress existing = courseProgressRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tiến trình khóa học"));

        existing.setCompleted(dto.completed());
        existing.setCompletedAt(dto.completed() ? java.time.LocalDateTime.now() : null);

        CourseProgress saved = courseProgressRepo.save(existing);
        return courseProgressMapper.toResponseDto(saved);
    }

    public void delete(CourseProgressKey id) {
        log.info("Xóa tiến trình khóa học {} của user {}", id.getCourseId(), id.getUserId());
        courseProgressRepo.deleteById(id);
    }

}
