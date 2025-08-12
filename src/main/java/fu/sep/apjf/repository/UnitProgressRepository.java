package fu.sep.apjf.repository;

import fu.sep.apjf.entity.UnitProgress;
import fu.sep.apjf.entity.UnitProgressKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitProgressRepository extends JpaRepository<UnitProgress, UnitProgressKey> {

    List<UnitProgress> findByUserId(Long userId);

    List<UnitProgress> findByUserIdAndUnit_ChapterId(Long userId, String chapterId);

    boolean existsByUnitIdAndUserIdAndCompletedTrue(String unitId, Long userId);

    List<UnitProgress> findByUserIdAndUnit_Chapter_CourseId(Long userId, String courseId);
}
