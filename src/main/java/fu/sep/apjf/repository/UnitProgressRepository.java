package fu.sep.apjf.repository;

import fu.sep.apjf.entity.UnitProgress;
import fu.sep.apjf.entity.UnitProgressKey;
import fu.sep.apjf.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnitProgressRepository extends JpaRepository<UnitProgress, UnitProgressKey> {
    long countByUserAndUnitCourseIdAndCompletedTrue(User user, String courseId);
}

