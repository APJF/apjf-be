package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.LearningPathRequestDto;
import fu.sep.apjf.dto.response.*;
import fu.sep.apjf.entity.*;
import fu.sep.apjf.exception.ResourceNotFoundException;
import fu.sep.apjf.mapper.CourseLearningPathMapper;
import fu.sep.apjf.mapper.LearningPathMapper;
import fu.sep.apjf.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class LearningPathService {

    private final LearningPathRepository learningPathRepository;
    private final CourseLearningPathRepository courseLearningPathRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final LearningPathMapper learningPathMapper;
    private final CourseLearningPathMapper courseLearningPathMapper;
    private final CourseProgressRepository courseProgressRepository;
    private final UnitProgressRepository unitProgressRepository;

    @Transactional(readOnly = true)
    public LearningPathDetailResponseDto getLearningPathById(Long id, User user) {
        LearningPath path = learningPathRepository.findByIdWithCourses(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy Learning Path với ID: " + id));

        // Lấy tất cả CourseLearningPath
        List<CourseLearningPath> courseLearningPaths = path.getCourseLearningPaths();

        // Lấy danh sách course
        List<Course> courses = courseLearningPaths.stream()
                .map(CourseLearningPath::getCourse)
                .toList();

        // Lấy tất cả progress của user liên quan đến các course này
        List<CourseProgress> progresses = courseProgressRepository.findByUserId(user.getId());
        Map<String, CourseProgress> progressMap = progresses.stream()
                .filter(cp -> courses.stream().anyMatch(c -> c.getId().equals(cp.getCourse().getId())))
                .collect(Collectors.toMap(cp -> cp.getCourse().getId(), cp -> cp));

        // Map course -> CourseDetailResponseDto
        List<CourseDetailResponseDto> courseDtos = courses.stream().map(course -> {
            // Lấy progress tương ứng
            CourseProgress progress = progressMap.get(course.getId());
            CourseProgressResponseDto progressDto = null;
            if (progress != null) {
                progressDto = new CourseProgressResponseDto(
                        progress.isCompleted(),
                        calculateCourseProgress(user, course.getId())
                );
            }

            return new CourseDetailResponseDto(
                    course.getId(),
                    course.getTitle(),
                    course.getDescription(),
                    course.getDuration(),
                    course.getLevel(),
                    null, // image
                    null, // requirement
                    null, // status
                    null, // prerequisiteCourseId
                    null, // topics
                    null, // averageRating
                    progressDto
            );
        }).toList();

        // Tính tổng progress cho Learning Path
        long completedCourses = courseDtos.stream()
                .filter(c -> c.courseProgress() != null && c.courseProgress().completed())
                .count();
        float percent = courses.isEmpty() ? 0f : (completedCourses * 100f / courses.size());
        EnumClass.Level targetLevel = EnumClass.Level.valueOf(path.getTargetLevel());

        return new LearningPathDetailResponseDto(
                path.getId(),
                path.getTitle(),
                path.getDescription(),
                targetLevel,
                path.getPrimaryGoal(),
                path.getFocusSkill(),
                path.getStatus(),
                path.getDuration(),
                user.getId(),
                user.getUsername(),
                path.getCreatedAt(),
                path.getLastUpdatedAt(),
                completedCourses == courses.size() && !courses.isEmpty(),
                percent,
                courseDtos
        );
    }


    @Transactional
    public LearningPathResponseDto createLearningPath(LearningPathRequestDto dto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        if (dto.courseIds() == null || dto.courseIds().isEmpty()) {
            throw new IllegalArgumentException("Phải chọn ít nhất một khóa học cho lộ trình.");
        }

        LearningPath entity = learningPathMapper.toEntity(dto, user);
        entity.setStatus(EnumClass.PathStatus.PENDING);
        LearningPath saved = learningPathRepository.save(entity);

        saveCourseLearningPaths(saved, dto.courseIds());

        List<CourseOrderDto> courseDtos = courseLearningPathRepository.findByLearningPathId(saved.getId()).stream()
                .map(courseLearningPathMapper::toDto)
                .toList();

        return learningPathMapper.toResponseDto(saved, courseDtos);
    }

    @Transactional
    public LearningPathResponseDto updateLearningPath(Long id, LearningPathRequestDto dto, Long userId) {
        LearningPath path = learningPathRepository.findById(id).orElseThrow();

        path.setTitle(dto.title());
        path.setDescription(dto.description());
        path.setTargetLevel(dto.targetLevel() != null ? dto.targetLevel().toString() : null);
        path.setPrimaryGoal(dto.primaryGoal());
        path.setFocusSkill(dto.focusSkill());
        path.setDuration(dto.duration());
        path.setLastUpdatedAt(Instant.now());
        learningPathRepository.save(path);

        if (dto.courseIds() != null) {
            courseLearningPathRepository.deleteAll(courseLearningPathRepository.findByLearningPathId(path.getId()));
            saveCourseLearningPaths(path, dto.courseIds());
        }

        List<CourseOrderDto> courseDtos = courseLearningPathRepository.findByLearningPathId(path.getId()).stream()
                .map(courseLearningPathMapper::toDto)
                .toList();

        return learningPathMapper.toResponseDto(path, courseDtos);
    }

    public void deleteLearningPath(Long id) {
        courseLearningPathRepository.deleteAll(courseLearningPathRepository.findByLearningPathId(id));
        learningPathRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<LearningPathDetailResponseDto> getLearningPathsByUser(Long userId) {
        List<LearningPath> paths = learningPathRepository.findByUserIdWithCourses(userId);

        return paths.stream().map(path -> {
            List<Course> courses = path.getCourseLearningPaths().stream()
                    .map(CourseLearningPath::getCourse)
                    .toList();

            long completedCourses = courses.stream()
                    .filter(course -> courseProgressRepository
                            .existsByCourseAndUserIdAndCompleted(course, userId, true))
                    .count();

            float percent = courses.isEmpty() ? 0f : (completedCourses * 100f / courses.size());
            EnumClass.Level targetLevel = EnumClass.Level.valueOf(path.getTargetLevel());

            return new LearningPathDetailResponseDto(
                    path.getId(),
                    path.getTitle(),
                    path.getDescription(),
                    targetLevel,
                    path.getPrimaryGoal(),
                    path.getFocusSkill(),
                    path.getStatus(),
                    path.getDuration(),
                    path.getUser().getId(),
                    path.getUser().getUsername(),
                    path.getCreatedAt(),
                    path.getLastUpdatedAt(),
                    completedCourses == courses.size() && !courses.isEmpty(),
                    percent,
                    null
            );
        }).toList();
    }

    public void addCourseToLearningPath(Long learningPathId, CourseOrderDto dto) {
        Course course = courseRepository.findById(dto.courseId()).orElseThrow();
        LearningPath path = learningPathRepository.findById(learningPathId).orElseThrow();

        boolean exists = courseLearningPathRepository
                .findByLearningPathId(learningPathId).stream()
                .anyMatch(clp -> clp.getCourse().getId().equals(dto.courseId()));

        if (exists) {
            throw new IllegalArgumentException("Khóa học đã tồn tại trong lộ trình.");
        }

        // Tạo CourseLearningPath với learningPathId từ parameter, không từ DTO
        CourseLearningPath entity = new CourseLearningPath(
                new CourseLearningPathKey(dto.courseId(), learningPathId),
                course,
                path,
                dto.courseOrderNumber()
        );
        courseLearningPathRepository.save(entity);
    }

    public void removeCourseFromLearningPath(String courseId, Long learningPathId) {
        courseLearningPathRepository.deleteByLearningPathIdAndCourseId(learningPathId, courseId);
    }

    @Transactional
    public void setStudyingLearningPath(Long userId, Long learningPathId) {
        learningPathRepository.updateStatusByUserIdAndStatus(
                userId, EnumClass.PathStatus.STUDYING, EnumClass.PathStatus.PENDING
        );
        LearningPath path = learningPathRepository.findById(learningPathId).orElseThrow();
        path.setStatus(EnumClass.PathStatus.STUDYING);
        path.setLastUpdatedAt(Instant.now());
        learningPathRepository.save(path);
    }

    @Transactional(readOnly = true)
    public LearningPathDetailResponseDto getStudyingLearningPath(Long userId) {
        // Lấy learning path có status = STUDYING của user
        LearningPath path = learningPathRepository.findByUserIdAndStatus(userId, EnumClass.PathStatus.STUDYING)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Learning Path đang học"));

        // Lấy tất cả course trong learning path
        List<Course> courses = path.getCourseLearningPaths().stream()
                .map(CourseLearningPath::getCourse)
                .toList();

        // Lấy progress của user cho các course
        List<CourseProgress> progresses = courseProgressRepository.findByUserId(userId);
        Map<String, CourseProgress> progressMap = progresses.stream()
                .filter(cp -> courses.stream().anyMatch(c -> c.getId().equals(cp.getCourse().getId())))
                .collect(Collectors.toMap(cp -> cp.getCourse().getId(), cp -> cp));

        // Map course -> DTO
        List<CourseDetailResponseDto> courseDtos = courses.stream().map(course -> {
            CourseProgress progress = progressMap.get(course.getId());
            CourseProgressResponseDto progressDto = null;
            if (progress != null) {
                progressDto = new CourseProgressResponseDto(
                        progress.isCompleted(),
                        calculateCourseProgress(progress.getUser(), course.getId())
                );
            }

            return new CourseDetailResponseDto(
                    course.getId(),
                    course.getTitle(),
                    course.getDescription(),
                    course.getDuration(),
                    course.getLevel(),
                    null, // image
                    null, // requirement
                    null, // status
                    null, // prerequisiteCourseId
                    null, // topics
                    null, // averageRating
                    progressDto
            );
        }).toList();

        // Tính progress tổng thể của Learning Path
        long completedCourses = courseDtos.stream()
                .filter(c -> c.courseProgress() != null && c.courseProgress().completed())
                .count();
        float percent = courses.isEmpty() ? 0f : (completedCourses * 100f / courses.size());
        EnumClass.Level targetLevel = EnumClass.Level.valueOf(path.getTargetLevel());

        return new LearningPathDetailResponseDto(
                path.getId(),
                path.getTitle(),
                path.getDescription(),
                targetLevel,
                path.getPrimaryGoal(),
                path.getFocusSkill(),
                path.getStatus(),
                path.getDuration(),
                userId,
                path.getUser().getUsername(),
                path.getCreatedAt(),
                path.getLastUpdatedAt(),
                completedCourses == courses.size() && !courses.isEmpty(),
                percent,
                courseDtos
        );
    }


    @Transactional
    public void reorderCoursesInPath(Long pathId, List<String> courseIds) {
        List<CourseLearningPath> currentCourses = courseLearningPathRepository.findByLearningPathId(pathId);
        Map<String, CourseLearningPath> courseMap = new HashMap<>();
        for (CourseLearningPath clp : currentCourses) {
            courseMap.put(clp.getCourse().getId(), clp);
        }

        for (int i = 0; i < courseIds.size(); i++) {
            String courseId = courseIds.get(i);
            CourseLearningPath clp = courseMap.get(courseId);
            if (clp != null) {
                clp.setCourseOrderNumber(i);
                courseLearningPathRepository.save(clp);
            }
        }
    }

    private void saveCourseLearningPaths(LearningPath path, List<String> courseIds) {
        List<CourseLearningPath> courseLinks = IntStream.range(0, courseIds.size())
                .mapToObj(i -> {
                    String courseId = courseIds.get(i);
                    Course course = courseRepository.findById(courseId)
                            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khóa học: " + courseId));
                    return new CourseLearningPath(
                            new CourseLearningPathKey(courseId, path.getId()), course, path, i);
                })
                .toList();
        courseLearningPathRepository.saveAll(courseLinks);
    }

    public float calculateCourseProgress(User user, String courseId) {
        long totalChapters = unitProgressRepository.countChaptersByCourseId(courseId);
        if (totalChapters == 0) return 0;

        long completedChapters = unitProgressRepository.countCompletedChaptersByUserAndCourse(user, courseId);

        return (completedChapters * 100.0f) / totalChapters;
    }
}