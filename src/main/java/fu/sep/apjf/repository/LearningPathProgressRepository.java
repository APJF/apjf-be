package fu.sep.apjf.repository;

import fu.sep.apjf.entity.LearningPathProgress;
import fu.sep.apjf.entity.LearningPathProgressKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LearningPathProgressRepository extends JpaRepository<LearningPathProgress, LearningPathProgressKey> {

    List<LearningPathProgress> findByUserId(Long userId);

    boolean existsByLearningPathIdAndUserIdAndCompletedTrue(Long learningPathId, Long userId);
}
