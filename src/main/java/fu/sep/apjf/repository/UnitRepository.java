package fu.sep.apjf.repository;

import fu.sep.apjf.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitRepository extends JpaRepository<Unit, String> {

    List<Unit> findByChapterId(String chapterId);
    List<Unit> findByChapterIdIn(List<String> chapterIds);
    @Query("SELECT COUNT(u) FROM Unit u WHERE u.chapter.id = :chapterId")
    long countUnitsByChapterId(@Param("chapterId") String chapterId);

    @Query("SELECT COUNT(up) FROM UnitProgress up " +
            "WHERE up.user.id = :userId " +
            "AND up.unit.chapter.id = :chapterId " +
            "AND up.completed = true")
    long countCompletedUnitsByUserAndChapter(@Param("userId") Long userId,
                                             @Param("chapterId") String chapterId);
}