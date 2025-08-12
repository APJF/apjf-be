package fu.sep.apjf.repository;

import fu.sep.apjf.entity.ChapterProgress;
import fu.sep.apjf.entity.ChapterProgressKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChapterProgressRepository extends JpaRepository<ChapterProgress, ChapterProgressKey> {
    List<ChapterProgress> findByUserId(Long userId);

    boolean existsByChapterIdAndUserIdAndCompletedTrue(String chapterId, Long userId);

    @Query("""
        SELECT cp
        FROM ChapterProgress cp
        WHERE cp.user.id = :userId
          AND cp.chapter.course.id = :courseId
    """)
    List<ChapterProgress> findByUserIdAndCourseId(@Param("userId") Long userId,
                                                  @Param("courseId") String courseId);
}
