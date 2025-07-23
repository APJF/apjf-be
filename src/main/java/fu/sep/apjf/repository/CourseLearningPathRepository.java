package fu.sep.apjf.repository;

import fu.sep.apjf.entity.CourseLearningPath;
import fu.sep.apjf.entity.CourseLearningPathKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseLearningPathRepository extends JpaRepository<CourseLearningPath, CourseLearningPathKey> {
    List<CourseLearningPath> findByLearningPathId(Long learningPathId);
    void deleteByLearningPathIdAndCourseId(Long learningPathId, String courseId);
}
