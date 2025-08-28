package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.LearningPathRequestDto;
import fu.sep.apjf.dto.response.CourseOrderDto;
import fu.sep.apjf.dto.response.LearningPathDetailResponseDto;
import fu.sep.apjf.dto.response.LearningPathResponseDto;
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
    private final LearningPathProgressRepository learningPathProgressRepository;

    public LearningPathDetailResponseDto getLearningPathById(Long id) {
        LearningPath path = learningPathRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Learning Path với ID: " + id));

        // Lấy tất cả Course từ các CourseLearningPath
        List<Course> courses = path.getCourseLearningPaths().stream()
                .map(CourseLearningPath::getCourse)
                .toList();

        // Tính % hoàn thành theo số lượng course đã complete
        long completedCourses = courses.stream()
                .filter(course -> courseProgressRepository
                        .existsByCourseAndUserIdAndCompleted(course, path.getUser().getId(), true))
                .count();
        float percent = courses.isEmpty() ? 0f : (completedCourses * 100f / courses.size());

        // Convert targetLevel từ String sang Enum
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
                percent
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

    public List<LearningPathDetailResponseDto> getLearningPathsByUser(Long userId) {
        List<LearningPath> paths = learningPathRepository.findByUserId(userId);

        return paths.stream().map(path -> {
            // Lấy tất cả Course từ các CourseLearningPath
            List<Course> courses = path.getCourseLearningPaths().stream()
                    .map(CourseLearningPath::getCourse)
                    .toList();

            // Tính % hoàn thành theo số lượng course đã complete
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
                    percent
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
}