package fu.sep.apjf.service;

import fu.sep.apjf.entity.*;
import fu.sep.apjf.exception.ResourceNotFoundException;
import fu.sep.apjf.mapper.ChapterMapper;
import fu.sep.apjf.mapper.MaterialMapper;
import fu.sep.apjf.mapper.UnitMapper;
import fu.sep.apjf.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgressTrackingService {

    private final UnitRepository unitRepository;
    private final ChapterRepository chapterRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final UnitProgressRepository unitProgressRepository;
    private final CourseProgressRepository courseProgressRepository;
    private final ChapterProgressRepository chapterProgressRepository;
    private final CourseLearningPathRepository courseLearningPathRepository;
    private final LearningPathProgressRepository learningPathProgressRepository;
    private final LearningPathRepository learningPathRepository;
    private final ExamRepository examRepository;
    private final ExamResultRepository examResultRepository;

    @Transactional
    public void markUnitPassed(String unitId, Long userId) {
        Unit unit = unitRepository.findById(unitId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();

        // Lưu trạng thái unit đã hoàn thành
        UnitProgress progress = UnitProgress.builder()
                .id(new UnitProgressKey(unitId, userId))
                .unit(unit)
                .user(user)
                .completed(true)
                .build();
        unitProgressRepository.save(progress);

        // Kiểm tra Chapter
        markChapterComplete(unit.getChapter().getId(), userId);
    }


    @Transactional
    public void markChapterComplete(String chapterId, Long userId) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Chapter"));

        User user = userRepository.findById(userId).orElseThrow();

        // Lấy tất cả Units trong Chapter
        List<Unit> units = unitRepository.findByChapterId(chapterId);

        // Kiểm tra xem tất cả Units đã hoàn thành chưa
        boolean allUnitsCompleted = units.stream().allMatch(unit ->
                unitProgressRepository
                        .findById(new UnitProgressKey(unit.getId(), userId))
                        .map(UnitProgress::isCompleted)
                        .orElse(false)
        );

        List<Exam> examsInChapter = examRepository.findByChapterId(chapterId);
        boolean allExamsPassed = examsInChapter.stream().allMatch(exam ->
                examResultRepository
                        .findByUserIdAndIdExamId(userId, exam.getId())
                        .map(result -> result.getStatus() == EnumClass.ExamStatus.PASSED)
                        .orElse(false)
        );

        if (allUnitsCompleted && allExamsPassed) {
            ChapterProgress progress = ChapterProgress.builder()
                    .id(new ChapterProgressKey(chapterId, userId))
                    .chapter(chapter)
                    .user(user)
                    .completed(true)
                    .build();
            chapterProgressRepository.save(progress);

            // Kiểm tra Course sau khi Chapter hoàn thành
            markCourseComplete(chapter.getCourse().getId(), userId);
        }
    }

    @Transactional
    public void markCourseComplete(String courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Course"));

        User user = userRepository.findById(userId).orElseThrow();

        // Lấy tất cả Chapters trong Course
        List<Chapter> chapters = chapterRepository.findByCourseId(courseId);

        // Kiểm tra xem tất cả Chapters đã hoàn thành chưa
        boolean allChaptersCompleted = chapters.stream().allMatch(chapter ->
                chapterProgressRepository
                        .findById(new ChapterProgressKey(chapter.getId(), userId))
                        .map(ChapterProgress::isCompleted)
                        .orElse(false)
        );

        List<Exam> examsInCourse = examRepository.findByCourseId(courseId);
        boolean allExamsPassed = examsInCourse.stream().allMatch(exam ->
                examResultRepository
                        .findByUserIdAndIdExamId(userId, exam.getId())
                        .map(result -> result.getStatus() == EnumClass.ExamStatus.PASSED)
                        .orElse(false)
        );

        if (allChaptersCompleted && allExamsPassed) {
            // Đánh dấu Course là completed
            CourseProgress progress = CourseProgress.builder()
                    .id(new CourseProgressKey(courseId, userId))
                    .course(course)
                    .user(user)
                    .completed(true)
                    .build();
            courseProgressRepository.save(progress);

            // === Xử lý Learning Path ===
            // Lấy tất cả các learning path chứa course này
            List<CourseLearningPath> mappings = courseLearningPathRepository.findByCourseId(courseId);
            for (CourseLearningPath mapping : mappings) {
                markLearningPathComplete(mapping.getLearningPath().getId(), userId);
            }
        }
    }

    @Transactional
    public void markLearningPathComplete(Long learningPathId, Long userId) {
        LearningPath lp = learningPathRepository.findById(learningPathId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Learning Path"));

        User user = userRepository.findById(userId).orElseThrow();

        // Lấy tất cả Course thuộc Learning Path qua bảng trung gian
        List<CourseLearningPath> mappings = courseLearningPathRepository.findByLearningPathId(learningPathId);

        List<Course> courses = mappings.stream()
                .map(CourseLearningPath::getCourse)
                .toList();

        // Kiểm tra tất cả course đã xong chưa
        boolean allCoursesCompleted = courses.stream().allMatch(course ->
                courseProgressRepository
                        .findById(new CourseProgressKey(course.getId(), userId))
                        .map(CourseProgress::isCompleted)
                        .orElse(false)
        );

        if (allCoursesCompleted) {
            LearningPathProgress progress = LearningPathProgress.builder()
                    .id(new LearningPathProgressKey(learningPathId, userId))
                    .learningPath(lp)
                    .user(user)
                    .completed(true)
                    .build();
            learningPathProgressRepository.save(progress);
        }
    }

}
