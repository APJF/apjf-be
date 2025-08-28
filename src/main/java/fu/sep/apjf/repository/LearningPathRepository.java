package fu.sep.apjf.repository;

import fu.sep.apjf.entity.EnumClass;
import fu.sep.apjf.entity.LearningPath;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LearningPathRepository extends JpaRepository<LearningPath, Long> {
    @Query("""
        SELECT lp FROM LearningPath lp
        LEFT JOIN FETCH lp.courseLearningPaths clp
        LEFT JOIN FETCH clp.course
        WHERE lp.user.id = :userId
    """)
    List<LearningPath> findByUserIdWithCourses(@Param("userId") Long userId);

    @Query("""
        SELECT lp FROM LearningPath lp
        LEFT JOIN FETCH lp.courseLearningPaths clp
        LEFT JOIN FETCH clp.course
        WHERE lp.id = :id
    """)
    Optional<LearningPath> findByIdWithCourses(@Param("id") Long id);



    Optional<LearningPath> findByUserIdAndStatus(Long userId, EnumClass.PathStatus status);

    @Transactional
    @Modifying
    @Query("UPDATE LearningPath lp SET lp.status = :newStatus WHERE lp.user.id = :userId AND lp.status = :currentStatus")
    void updateStatusByUserIdAndStatus(Long userId, EnumClass.PathStatus currentStatus, EnumClass.PathStatus newStatus);
}
