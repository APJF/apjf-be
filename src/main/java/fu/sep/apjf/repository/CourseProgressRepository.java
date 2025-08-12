package fu.sep.apjf.repository;

import fu.sep.apjf.entity.CourseProgress;
import fu.sep.apjf.entity.CourseProgressKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseProgressRepository extends JpaRepository<CourseProgress, CourseProgressKey> {

    List<CourseProgress> findByUserId(Long userId);

    boolean existsByCourseIdAndUserIdAndCompletedTrue(String courseId, Long userId);
}
