package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.LearningPathRequestDto;
import fu.sep.apjf.dto.response.CourseOrderDto;
import fu.sep.apjf.dto.response.LearningPathResponseDto;
import fu.sep.apjf.dto.response.UnitProgressDto;
import fu.sep.apjf.entity.*;
import fu.sep.apjf.mapper.CourseLearningPathMapper;
import fu.sep.apjf.mapper.LearningPathMapper;
import fu.sep.apjf.mapper.UnitProgressMapper;
import fu.sep.apjf.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private final LearningPathMapper learningPathMapper;
    private final CourseLearningPathMapper courseLearningPathMapper;
    private final UnitProgressMapper unitProgressMapper;

    public LearningPathResponseDto createLearningPath(LearningPathRequestDto dto) {
        User user = userRepository.findById(dto.userId()).orElseThrow();
        LearningPath entity = learningPathMapper.toEntity(dto, user);
        return learningPathMapper.toDto(learningPathRepository.save(entity));
    }

    public LearningPathResponseDto updateLearningPath(Long id, LearningPathRequestDto dto) {
        LearningPath entity = learningPathRepository.findById(id).orElseThrow();
        entity.setTitle(dto.title());
        entity.setDescription(dto.description());
        entity.setTargetLevel(dto.targetLevel() != null ? dto.targetLevel().toString() : null);
        entity.setPrimaryGoal(dto.primaryGoal());
        entity.setFocusSkill(dto.focusSkill());
        entity.setStatus(dto.status() != null ? EnumClass.PathStatus.valueOf(dto.status().name()) : null);
        entity.setDuration(dto.duration() != null ? new BigDecimal(dto.duration()) : null);
        entity.setLastUpdatedAt(LocalDateTime.now());
        return learningPathMapper.toDto(learningPathRepository.save(entity));
    }

    public void deleteLearningPath(Long id) {
        learningPathRepository.deleteById(id);
    }

    public List<LearningPathResponseDto> getLearningPathsByUser(Long userId) {
        return learningPathRepository.findByUserId(userId).stream()
                .map(learningPathMapper::toDto)
                .toList();
    }

    public void addCourseToLearningPath(CourseOrderDto dto) {
        Course course = courseRepository.findById(dto.courseId()).orElseThrow();
        LearningPath path = learningPathRepository.findById(dto.learningPathId()).orElseThrow();
        CourseLearningPath entity = courseLearningPathMapper.toEntity(dto, course, path);
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
                .passed(true)
                .passedAt(LocalDateTime.now())
                .build();
        return unitProgressMapper.toDto(unitProgressRepository.save(progress));
    }
    /*
    public boolean isChapterPassed(Long chapterId, Long userId) {
        List<Unit> allUnits = unitRepository.findByChapterId(chapterId);
        List<UnitProgress> passedUnits = unitProgressRepository.findByUserIdAndUnit_ChapterId(userId, chapterId);
        return allUnits.size() > 0 && allUnits.size() == passedUnits.size();
    }
     */
}
