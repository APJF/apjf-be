package fu.sep.apjf.repository;

import fu.sep.apjf.entity.UnitProgress;
import fu.sep.apjf.entity.UnitProgressKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UnitProgressRepository extends JpaRepository<UnitProgress, UnitProgressKey> {
    List<UnitProgress> findByUserId(Long userId);
    List<UnitProgress> findByUserIdAndUnit_ChapterId(Long userId, String chapterId);
}
