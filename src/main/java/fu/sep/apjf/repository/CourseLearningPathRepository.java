package fu.sep.apjf.repository;

import fu.sep.apjf.entity.CourseLearningPath;
import fu.sep.apjf.entity.CourseLearningPathKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseLearningPathRepository extends JpaRepository<CourseLearningPath, CourseLearningPathKey> {
    void deleteByLearningPathIdAndCourseId(Long learningPathId, String courseId);
}
