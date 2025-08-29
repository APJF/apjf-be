package fu.sep.apjf.repository;

import fu.sep.apjf.entity.Unit;
import fu.sep.apjf.entity.UnitProgress;
import fu.sep.apjf.entity.UnitProgressKey;
import fu.sep.apjf.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UnitProgressRepository extends JpaRepository<UnitProgress, Long> {
    Optional<UnitProgress> findById(UnitProgressKey id);

    // Check tồn tại progress của 1 user cho 1 unit
    boolean existsByUserAndUnit(User user, Unit unit);

    // Nếu chỉ có id
    @Query("SELECT up FROM UnitProgress up WHERE up.user = :user AND up.unit.chapter.id = :chapterId")
    List<UnitProgress> findByUserAndChapter(@Param("user") User user, @Param("chapterId") String chapterId);

    // Đếm tổng số Unit của 1 course
    @Query("""
        SELECT COUNT(c) 
        FROM Chapter c 
        WHERE c.course.id = :courseId
          AND c.status = 'ACTIVE'
    """)
    long countActiveChaptersByCourseId(@Param("courseId") String courseId);

    // Đếm số Chapter ACTIVE đã hoàn thành của User trong Course
    @Query("""
        SELECT COUNT(cp) 
        FROM ChapterProgress cp
        WHERE cp.user = :user
          AND cp.chapter.course.id = :courseId
          AND cp.chapter.status = 'ACTIVE'
          AND cp.completed = true
    """)
    long countCompletedActiveChaptersByUserAndCourse(@Param("user") User user,
                                                     @Param("courseId") String courseId);

    Optional<UnitProgress> findByUserIdAndUnitId(Long userId, String unitId);
}
