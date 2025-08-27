package fu.sep.apjf.repository;

import fu.sep.apjf.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, String> {
    List<Chapter> findByCourseId(String courseId);
    @Query("""
        SELECT CASE WHEN COUNT(cp) > 0 THEN TRUE ELSE FALSE END
        FROM ChapterProgress cp
        WHERE cp.chapter.id = :chapterId
          AND cp.user.id = :userId
          AND cp.completed = TRUE
    """)
    boolean isChapterCompleted(@Param("chapterId") String chapterId,
                               @Param("userId") Long userId);
}