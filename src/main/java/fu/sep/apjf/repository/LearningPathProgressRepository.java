package fu.sep.apjf.repository;

import fu.sep.apjf.entity.LearningPathProgress;
import fu.sep.apjf.entity.LearningPathProgressKey;
import fu.sep.apjf.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearningPathProgressRepository extends JpaRepository<LearningPathProgress, LearningPathProgressKey> {

    // Tìm tiến trình của 1 user với 1 Learning Path
    Optional<LearningPathProgress> findById(LearningPathProgressKey id);

}

