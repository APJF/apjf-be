package fu.sep.apjf.repository;

import fu.sep.apjf.entity.Course;
import fu.sep.apjf.entity.EnumClass;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, String>, JpaSpecificationExecutor<Course> {
    @NotNull
    @Override
    @EntityGraph(attributePaths = {
            "chapters",
            "chapters.units"
    })
    Optional<Course> findById(@NotNull String id);

    @NotNull
    @Override
    @EntityGraph(attributePaths = {
            "chapters",
            "chapters.units"
    })
    Page<Course> findAll(@NotNull Pageable pageable);

    List<Course> findByStatus(EnumClass.Status status);

    Page<Course> findByStatus(EnumClass.Status status, Pageable pageable);

    List<Course> findByLevel(EnumClass.Level level);

    Page<Course> findByLevel(EnumClass.Level level, Pageable pageable);

    List<Course> findByPrerequisiteCourse(Course prerequisiteCourse);

    List<Course> findByPrerequisiteCourseIsNull();

    List<Course> findByTitleContainingIgnoreCase(String title);

    Page<Course> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    List<Course> findByDescriptionContainingIgnoreCase(String description);

    List<Course> findByTitleStartingWithIgnoreCase(String titlePrefix);

    List<Course> findByDurationGreaterThanEqual(BigDecimal minDuration);

    List<Course> findByDurationLessThanEqual(BigDecimal maxDuration);

    List<Course> findByDurationBetween(BigDecimal minDuration, BigDecimal maxDuration);

    List<Course> findByStatusAndLevel(EnumClass.Status status, EnumClass.Level level);

    Page<Course> findByStatusAndLevel(EnumClass.Status status, EnumClass.Level level, Pageable pageable);

    long countByStatus(EnumClass.Status status);

    long countByLevel(EnumClass.Level level);

    boolean existsByTitleIgnoreCase(String title);
}