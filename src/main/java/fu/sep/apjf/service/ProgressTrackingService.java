//package fu.sep.apjf.service;
//
//import fu.sep.apjf.entity.*;
//import fu.sep.apjf.repository.*;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class ProgressTrackingService {
//
//    private final UnitRepository unitRepository;
//    private final ChapterRepository chapterRepository;
//    private final CourseRepository courseRepository;
//    private final UserRepository userRepository;
//    private final UnitProgressRepository unitProgressRepository;
//
//    @Transactional
//    public void markUnitPassed(String unitId, Long userId) {
//        Unit unit = unitRepository.findById(unitId).orElseThrow();
//        User user = userRepository.findById(userId).orElseThrow();
//
//        // Nếu đã pass rồi thì không làm gì
//        if (unitProgressRepository.existsByUnitIdAndUserIdAndIsPassedTrue(unitId, userId)) return;
//
//        UnitProgress progress = UnitProgress.builder()
//                .id(new UnitProgressKey(unitId, userId))
//                .unit(unit)
//                .user(user)
//                .isPassed(true)
//                .passedAt(LocalDateTime.now())
//                .build();
//        unitProgressRepository.save(progress);
//
//        checkAndMarkChapterPassed(unit.getChapter().getId(), userId);
//    }
//
//    private void checkAndMarkChapterPassed(String chapterId, Long userId) {
//        List<Unit> units = unitRepository.findByChapterId(chapterId);
//        long passedCount = units.stream()
//                .filter(unit -> unitProgressRepository.existsByUnitIdAndUserIdAndIsPassedTrue(unit.getId(), userId))
//                .count();
//
//        if (passedCount == units.size() && units.size() > 0) {
//            Chapter chapter = chapterRepository.findById(chapterId).orElseThrow();
//            chapter.getProgress().put(userId, true); // Giả sử có map theo user, nếu không thì cần tạo ChapterProgress entity
//            chapterRepository.save(chapter);
//
//            checkAndMarkCoursePassed(chapter.getCourse().getId(), userId);
//        }
//    }
//
//    private void checkAndMarkCoursePassed(String courseId, Long userId) {
//        List<Chapter> chapters = chapterRepository.findByCourseId(courseId);
//        long passedChapters = chapters.stream()
//                .filter(chapter -> {
//                    List<Unit> units = unitRepository.findByChapterId(chapter.getId());
//                    return units.stream()
//                            .allMatch(unit -> unitProgressRepository.existsByUnitIdAndUserIdAndIsPassedTrue(unit.getId(), userId));
//                })
//                .count();
//
//        if (passedChapters == chapters.size() && chapters.size() > 0) {
//            Course course = courseRepository.findById(courseId).orElseThrow();
//            course.getProgress().put(userId, true); // Giả sử có map theo user, nếu không thì cần CourseProgress entity
//            courseRepository.save(course);
//        }
//    }
//}
