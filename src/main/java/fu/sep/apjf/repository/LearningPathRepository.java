package fu.sep.apjf.repository;

import fu.sep.apjf.entity.LearningPath;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LearningPathRepository extends JpaRepository<LearningPath, Long> {
    List<LearningPath> findByUserId(Long userId);
}
