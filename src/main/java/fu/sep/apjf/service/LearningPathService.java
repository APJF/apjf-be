package fu.sep.apjf.service;

import fu.sep.apjf.dto.*;
import fu.sep.apjf.entity.*;
import fu.sep.apjf.mapper.*;
import fu.sep.apjf.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LearningPathService {

    private final LearningPathRepository learningPathRepository;
    private final CourseLearningPathRepository courseLearningPathRepository;
    private final UnitProgressRepository unitProgressRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final UnitRepository unitRepository;
    private final ChapterRepository chapterRepository;

    public LearningPathDto createLearningPath(LearningPathDto dto) {
        User user = userRepository.findById(dto.userId()).orElseThrow();
        LearningPath entity = LearningPathMapper.toEntity(dto, user);
        return LearningPathMapper.toDto(learningPathRepository.save(entity));
    }

    public LearningPathDto updateLearningPath(Long id, LearningPathDto dto) {
        LearningPath entity = learningPathRepository.findById(id).orElseThrow();
        entity.setTitle(dto.title());
        entity.setDescription(dto.description());
        entity.setTargetLevel(dto.targetLevel());
        entity.setPrimaryGoal(dto.primaryGoal());
        entity.setFocusSkill(dto.focusSkill());
        entity.setStatus(dto.status());
        entity.setDuration(dto.duration());
        entity.setLastUpdatedAt(LocalDateTime.now());
        return LearningPathMapper.toDto(learningPathRepository.save(entity));
    }

    public void deleteLearningPath(Long id) {
        learningPathRepository.deleteById(id);
    }

    public List<LearningPathDto> getLearningPathsByUser(Long userId) {
        return learningPathRepository.findByUserId(userId).stream()
                .map(LearningPathMapper::toDto)
                .toList();
    }

    public void addCourseToLearningPath(CourseOrderDto dto) {
        Course course = courseRepository.findById(dto.courseId()).orElseThrow();
        LearningPath path = learningPathRepository.findById(dto.learningPathId()).orElseThrow();
        CourseLearningPath entity = CourseLearningPathMapper.toEntity(dto, course, path);
        courseLearningPathRepository.save(entity);
    }

    public void removeCourseFromLearningPath(String courseId, Long learningPathId) {
        courseLearningPathRepository.deleteByLearningPathIdAndCourseId(learningPathId, courseId);
    }

    @Transactional
    public UnitProgressDto markUnitPassed(String unitId, Long userId) {
        Unit unit = unitRepository.findById(unitId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        UnitProgress progress = UnitProgress.builder()
                .id(new UnitProgressKey(unitId, userId))
                .unit(unit)
                .user(user)
                .isPassed(true)
                .passedAt(LocalDateTime.now())
                .build();
        return UnitProgressMapper.toDto(unitProgressRepository.save(progress));
    }
    /*
    public boolean isChapterPassed(Long chapterId, Long userId) {
        List<Unit> allUnits = unitRepository.findByChapterId(chapterId);
        List<UnitProgress> passedUnits = unitProgressRepository.findByUserIdAndUnit_ChapterId(userId, chapterId);
        return allUnits.size() > 0 && allUnits.size() == passedUnits.size();
    }
     */
}
